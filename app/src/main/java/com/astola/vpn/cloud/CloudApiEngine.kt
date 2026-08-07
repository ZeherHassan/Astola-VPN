package com.astola.vpn.cloud

import com.astola.vpn.util.AppLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object CloudApiEngine {
    // Production Server Public Domain Configuration
    const val HARDCODED_SERVER_DOMAIN = "vpn.zeherhassan.com"
    var baseUrl = "https://$HARDCODED_SERVER_DOMAIN"

    /**
     * Fetches available VPN servers via GET /astola/v1/servers
     */
    suspend fun fetchServers(): String? {
        return withContext(Dispatchers.IO) {
            try {
                AppLogger.i("Fetching servers from $baseUrl/astola/v1/servers...")
                val url = URL("$baseUrl/astola/v1/servers")
                val conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                conn.requestMethod = "GET"

                if (conn.responseCode == 200) {
                    val jsonStr = conn.inputStream.bufferedReader().use { it.readText() }
                    AppLogger.s("Successfully fetched online servers from $HARDCODED_SERVER_DOMAIN!")
                    jsonStr
                } else {
                    AppLogger.d("Servers API returned status ${conn.responseCode}. Using default list.")
                    null
                }
            } catch (e: Exception) {
                AppLogger.d("Offline mode or unreachable host $HARDCODED_SERVER_DOMAIN: ${e.message}")
                null
            }
        }
    }

    /**
     * Fetches active ISP payload tweaks via GET /astola/v1/tweaks
     */
    suspend fun fetchTweaks(): String? {
        return withContext(Dispatchers.IO) {
            try {
                AppLogger.i("Fetching tweaks from $baseUrl/astola/v1/tweaks...")
                val url = URL("$baseUrl/astola/v1/tweaks")
                val conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                conn.requestMethod = "GET"

                if (conn.responseCode == 200) {
                    val jsonStr = conn.inputStream.bufferedReader().use { it.readText() }
                    AppLogger.s("Successfully updated ISP tweaks from cloud server!")
                    jsonStr
                } else {
                    AppLogger.d("Tweaks API returned status ${conn.responseCode}. Using local tweaks.")
                    null
                }
            } catch (e: Exception) {
                AppLogger.d("Offline mode: Loaded local ISP tweaks.")
                null
            }
        }
    }
}
