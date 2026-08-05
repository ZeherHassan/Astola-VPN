package com.astola.vpn.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

object PingUtil {

    /**
     * Measures TCP connection latency in milliseconds to target host & port.
     */
    suspend fun pingHost(host: String, port: Int = 443, timeoutMs: Int = 3000): Int {
        return withContext(Dispatchers.IO) {
            val startTime = System.currentTimeMillis()
            try {
                Socket().use { socket ->
                    socket.connect(InetSocketAddress(host, port), timeoutMs)
                }
                (System.currentTimeMillis() - startTime).toInt()
            } catch (e: Exception) {
                -1 // Timeout or Error
            }
        }
    }
}
