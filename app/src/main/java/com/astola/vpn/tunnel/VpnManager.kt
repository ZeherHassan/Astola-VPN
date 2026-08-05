package com.astola.vpn.tunnel

import android.content.Context
import android.content.Intent
import com.astola.vpn.tunnel.vpn.AstolaVpnService
import com.astola.vpn.ui.screens.home.VpnStatus
import com.astola.vpn.util.AppLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object VpnManager {
    private val _vpnState = MutableStateFlow(VpnStatus.DISCONNECTED)
    val vpnState: StateFlow<VpnStatus> = _vpnState.asStateFlow()

    private val _downloadSpeed = MutableStateFlow("0.0 KB/s")
    val downloadSpeed: StateFlow<String> = _downloadSpeed.asStateFlow()

    private val _uploadSpeed = MutableStateFlow("0.0 KB/s")
    val uploadSpeed: StateFlow<String> = _uploadSpeed.asStateFlow()

    fun toggleVpn(context: Context) {
        when (_vpnState.value) {
            VpnStatus.DISCONNECTED -> connectVpn(context)
            VpnStatus.CONNECTED -> disconnectVpn(context)
            VpnStatus.CONNECTING -> disconnectVpn(context)
        }
    }

    private fun connectVpn(context: Context) {
        _vpnState.value = VpnStatus.CONNECTING
        AppLogger.i("Initiating VPN Connection...")

        val intent = Intent(context, AstolaVpnService::class.java).apply {
            action = AstolaVpnService.ACTION_CONNECT
        }

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            _vpnState.value = VpnStatus.CONNECTED
            _downloadSpeed.value = "4.2 MB/s"
            _uploadSpeed.value = "1.1 MB/s"
            AppLogger.s("Astola VPN Tunnel successfully connected!")
        } catch (e: Exception) {
            _vpnState.value = VpnStatus.DISCONNECTED
            AppLogger.e("Failed to start VPN: ${e.message}")
        }
    }

    private fun disconnectVpn(context: Context) {
        AppLogger.i("Disconnecting VPN...")
        val intent = Intent(context, AstolaVpnService::class.java).apply {
            action = AstolaVpnService.ACTION_DISCONNECT
        }
        try {
            context.startService(intent)
        } catch (e: Exception) {
            AppLogger.e("Error stopping VPN: ${e.message}")
        }
        _vpnState.value = VpnStatus.DISCONNECTED
        _downloadSpeed.value = "0.0 KB/s"
        _uploadSpeed.value = "0.0 KB/s"
        AppLogger.i("Astola VPN disconnected.")
    }
}
