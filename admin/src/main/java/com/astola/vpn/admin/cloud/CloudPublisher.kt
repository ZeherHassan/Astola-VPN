package com.astola.vpn.admin.cloud

import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.logging.Logger

object CloudPublisher {
    private val logger = Logger.getLogger(CloudPublisher::class.java.name)

    /**
     * Publishes updated Server & ISP Tweak configurations to the cloud server endpoint.
     */
    fun publishToCloud(endpointUrl: String, jsonPayload: String, adminApiKey: String = "ADMIN_SECRET_KEY_2026"): Boolean {
        return try {
            logger.info("Publishing updated configurations to Cloud Endpoint: $endpointUrl")
            val url = URL(endpointUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 8000
            conn.readTimeout = 8000
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Authorization", "Bearer $adminApiKey")
            conn.doOutput = true

            val os: OutputStream = conn.outputStream
            os.write(jsonPayload.toByteArray(Charsets.UTF_8))
            os.flush()
            os.close()

            val responseCode = conn.responseCode
            logger.info("Cloud Publisher Response Code: $responseCode")
            responseCode in 200..299
        } catch (e: Exception) {
            logger.severe("Cloud Publisher failed: ${e.message}")
            false
        }
    }
}
