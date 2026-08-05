package com.astola.vpn.cloud

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.logging.Logger

object CloudServerRepository {
    private val logger = Logger.getLogger(CloudServerRepository::class.java.name)
    private const val DEFAULT_CLOUD_CONFIG_URL = "https://raw.githubusercontent.com/astola-vpn/configs/main/servers.json"

    /**
     * Downloads dynamic server and payload configurations from the remote cloud repository.
     */
    suspend fun fetchCloudConfigs(urlStr: String = DEFAULT_CLOUD_CONFIG_URL): String? {
        return withContext(Dispatchers.IO) {
            try {
                logger.info("Fetching remote cloud server list from $urlStr...")
                val url = URL(urlStr)
                val conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                conn.requestMethod = "GET"

                if (conn.responseCode == 200) {
                    val stream: InputStream = conn.inputStream
                    val jsonResponse = stream.bufferedReader().use { it.readText() }
                    logger.info("Successfully fetched remote server config (${jsonResponse.length} bytes)")
                    jsonResponse
                } else {
                    logger.warning("Cloud server fetch failed with HTTP ${conn.responseCode}")
                    null
                }
            } catch (e: Exception) {
                logger.severe("Error fetching cloud configs: ${e.message}")
                null
            }
        }
    }
}
