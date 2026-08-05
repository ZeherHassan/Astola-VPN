package com.astola.vpn.tunnel.vpn

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat
import com.astola.vpn.MainActivity
import com.astola.vpn.R
import java.util.logging.Logger

class AstolaVpnService : VpnService() {
    private val logger = Logger.getLogger(AstolaVpnService::class.java.name)
    private var vpnInterface: ParcelFileDescriptor? = null

    companion object {
        const val ACTION_CONNECT = "com.astola.vpn.ACTION_CONNECT"
        const val ACTION_DISCONNECT = "com.astola.vpn.ACTION_DISCONNECT"
        const val NOTIFICATION_CHANNEL_ID = "astola_vpn_channel"
        const val NOTIFICATION_ID = 101
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_CONNECT -> startVpn()
            ACTION_DISCONNECT -> stopVpn()
        }
        return START_STICKY
    }

    private fun startVpn() {
        try {
            logger.info("Starting Astola VpnService...")
            startForeground(NOTIFICATION_ID, buildNotification("Astola VPN Connected"))

            // Build Virtual TUN Network Interface
            val builder = Builder()
                .setSession("Astola VPN")
                .addAddress("10.0.0.2", 24)
                .addRoute("0.0.0.0", 0)
                .addDnsServer("1.1.1.1")
                .addDnsServer("8.8.8.8")
                .setMtu(1500)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                builder.setMetered(false)
            }

            vpnInterface = builder.establish()
            logger.info("TUN Virtual Interface Established (fd=${vpnInterface?.fd})")

            // Initialize tun2socks bridge
            vpnInterface?.fileDescriptor?.let { fd ->
                Tun2SocksBridge.start(fd.fd, "127.0.0.1:1080")
            }
        } catch (e: Exception) {
            logger.severe("Failed to start VpnService: ${e.message}")
            stopVpn()
        }
    }

    private fun stopVpn() {
        try {
            Tun2SocksBridge.stop()
            vpnInterface?.close()
            vpnInterface = null
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            logger.info("Astola VpnService Stopped.")
        } catch (e: Exception) {
            logger.severe("Error stopping VpnService: ${e.message}")
        }
    }

    private fun buildNotification(statusText: String): Notification {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Astola VPN Status",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Astola VPN Shield")
            .setContentText(statusText)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopVpn()
    }
}
