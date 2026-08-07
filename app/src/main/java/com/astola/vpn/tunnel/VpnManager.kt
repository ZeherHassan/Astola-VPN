package com.astola.vpn.tunnel

import android.content.Context
import android.content.Intent
import android.net.TrafficStats
import android.net.VpnService
import android.os.Process
import com.astola.vpn.cloud.CloudApiEngine
import com.astola.vpn.config.AstolaConfigModel
import com.astola.vpn.tunnel.ssh.SshConfig
import com.astola.vpn.tunnel.ssh.SshTunnelEngine
import com.astola.vpn.tunnel.transport.TransportType
import com.astola.vpn.tunnel.vpn.AstolaVpnService
import com.astola.vpn.ui.screens.home.VpnStatus
import com.astola.vpn.util.AppLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

object VpnManager {
    private val _vpnState = MutableStateFlow(VpnStatus.DISCONNECTED)
    val vpnState: StateFlow<VpnStatus> = _vpnState.asStateFlow()

    private val _downloadSpeed = MutableStateFlow("0.0 KB/s")
    val downloadSpeed: StateFlow<String> = _downloadSpeed.asStateFlow()

    private val _uploadSpeed = MutableStateFlow("0.0 KB/s")
    val uploadSpeed: StateFlow<String> = _uploadSpeed.asStateFlow()

    private val _sessionDuration = MutableStateFlow("00:00:00")
    val sessionDuration: StateFlow<String> = _sessionDuration.asStateFlow()

    private val _activeConfig = MutableStateFlow(
        AstolaConfigModel(
            title = "Astola Main Cloud Server Node",
            serverHost = CloudApiEngine.HARDCODED_SERVER_DOMAIN,
            serverPort = 443,
            username = "zeher",
            password = "zeher",
            payload = "GET / HTTP/1.1[crlf]Host: vpn.zeherhassan.com[crlf]Upgrade: websocket[crlf][crlf]",
            sniHost = "vpn.zeherhassan.com"
        )
    )
    val activeConfig: StateFlow<AstolaConfigModel> = _activeConfig.asStateFlow()

    private var activeSshEngine: SshTunnelEngine? = null
    private var speedMonitorJob: Job? = null
    private var durationJob: Job? = null
    private var connectedSeconds = 0L
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun updateConfig(config: AstolaConfigModel) {
        _activeConfig.value = config
    }

    /**
     * Checks if System VPN Permission is required before connecting.
     * Returns the prepare Intent if permission is needed, or null if already granted.
     */
    fun checkVpnPermission(context: Context): Intent? {
        return VpnService.prepare(context)
    }

    fun startVpnConnection(context: Context) {
        if (_vpnState.value != VpnStatus.DISCONNECTED) return

        _vpnState.value = VpnStatus.CONNECTING
        AppLogger.clear()
        AppLogger.i("Initializing Astola VPN Engine...")

        val currentConfig = _activeConfig.value

        coroutineScope.launch {
            try {
                // Step 1: Start Tunnel Engine
                AppLogger.i("Connecting to ${currentConfig.serverHost}:${currentConfig.serverPort} via ${currentConfig.protocol}...")
                
                val sshConfig = SshConfig(
                    host = currentConfig.serverHost,
                    port = currentConfig.serverPort,
                    username = currentConfig.username.ifBlank { "zeher" },
                    password = currentConfig.password ?: "zeher",
                    payload = currentConfig.payload,
                    sniHost = currentConfig.sniHost,
                    transportType = if (currentConfig.sniHost.isNotBlank()) TransportType.SSL_TLS else TransportType.DIRECT
                )

                val sshEngine = SshTunnelEngine(sshConfig)
                activeSshEngine = sshEngine

                val isTunnelStarted = sshEngine.startTunnel()
                if (!isTunnelStarted) {
                    AppLogger.e("Tunnel connection failed. Check server address/credentials.")
                    _vpnState.value = VpnStatus.DISCONNECTED
                    return@launch
                }

                AppLogger.s("Tunnel Engine connected. Launching Android VpnService...")

                // Step 2: Start Android VpnService
                val intent = Intent(context, AstolaVpnService::class.java).apply {
                    action = AstolaVpnService.ACTION_CONNECT
                }

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }

                _vpnState.value = VpnStatus.CONNECTED
                AppLogger.s("VPN Tunnel Active! Device traffic is now secured.")

                // Step 3: Start Live Traffic Metering & Timer
                startSpeedMonitor()
                startDurationTimer()

            } catch (e: Exception) {
                AppLogger.e("VPN Connection Error: ${e.message}")
                _vpnState.value = VpnStatus.DISCONNECTED
            }
        }
    }

    fun disconnectVpn(context: Context) {
        AppLogger.i("Disconnecting VPN Tunnel...")
        stopSpeedMonitor()
        stopDurationTimer()

        coroutineScope.launch {
            try {
                activeSshEngine?.stopTunnel()
                activeSshEngine = null

                val intent = Intent(context, AstolaVpnService::class.java).apply {
                    action = AstolaVpnService.ACTION_DISCONNECT
                }
                context.startService(intent)
            } catch (e: Exception) {
                AppLogger.e("Error stopping VPN Service: ${e.message}")
            } finally {
                _vpnState.value = VpnStatus.DISCONNECTED
                _downloadSpeed.value = "0.0 KB/s"
                _uploadSpeed.value = "0.0 KB/s"
                _sessionDuration.value = "00:00:00"
                AppLogger.i("Astola VPN Disconnected.")
            }
        }
    }

    private fun startSpeedMonitor() {
        speedMonitorJob?.cancel()
        speedMonitorJob = coroutineScope.launch {
            var prevRxBytes = TrafficStats.getUidRxBytes(Process.myUid())
            var prevTxBytes = TrafficStats.getUidTxBytes(Process.myUid())

            while (_vpnState.value == VpnStatus.CONNECTED) {
                delay(1000)
                val currentRxBytes = TrafficStats.getUidRxBytes(Process.myUid())
                val currentTxBytes = TrafficStats.getUidTxBytes(Process.myUid())

                val rxDiff = if (prevRxBytes > 0 && currentRxBytes >= prevRxBytes) currentRxBytes - prevRxBytes else 0
                val txDiff = if (prevTxBytes > 0 && currentTxBytes >= prevTxBytes) currentTxBytes - prevTxBytes else 0

                prevRxBytes = currentRxBytes
                prevTxBytes = currentTxBytes

                _downloadSpeed.value = formatSpeed(rxDiff)
                _uploadSpeed.value = formatSpeed(txDiff)
            }
        }
    }

    private fun stopSpeedMonitor() {
        speedMonitorJob?.cancel()
        speedMonitorJob = null
    }

    private fun startDurationTimer() {
        durationJob?.cancel()
        connectedSeconds = 0L
        durationJob = coroutineScope.launch {
            while (_vpnState.value == VpnStatus.CONNECTED) {
                val hrs = connectedSeconds / 3600
                val mins = (connectedSeconds % 3600) / 60
                val secs = connectedSeconds % 60
                _sessionDuration.value = String.format(Locale.US, "%02d:%02d:%02d", hrs, mins, secs)
                delay(1000)
                connectedSeconds++
            }
        }
    }

    private fun stopDurationTimer() {
        durationJob?.cancel()
        durationJob = null
    }

    private fun formatSpeed(bytesPerSec: Long): String {
        val kb = bytesPerSec / 1024.0
        return if (kb >= 1024) {
            String.format(Locale.US, "%.1f MB/s", kb / 1024.0)
        } else {
            String.format(Locale.US, "%.1f KB/s", kb)
        }
    }
}
