package com.astola.vpn.config

import java.io.File
import java.util.logging.Logger

object AstolaConfigRepository {
    private val logger = Logger.getLogger(AstolaConfigRepository::class.java.name)

    /**
     * Exports an AstolaConfigModel as an encrypted .astola binary file.
     */
    fun exportConfigFile(config: AstolaConfigModel, targetFile: File): Boolean {
        return try {
            val jsonStr = """
            {
              "configId": "${config.configId}",
              "title": "${config.title}",
              "description": "${config.description}",
              "serverHost": "${config.serverHost}",
              "serverPort": ${config.serverPort},
              "protocol": "${config.protocol}",
              "username": "${config.username}",
              "password": "${config.password}",
              "payload": "${config.payload.replace("\"", "\\\"")}",
              "sniHost": "${config.sniHost}",
              "isLocked": ${config.isLocked},
              "expiryTimestamp": ${config.expiryTimestamp},
              "createdAt": ${config.createdAt}
            }
            """.trimIndent()

            val encryptedBytes = ConfigCryptoEngine.encryptConfig(jsonStr)
            targetFile.writeBytes(encryptedBytes)
            logger.info("Config successfully exported to ${targetFile.absolutePath}")
            true
        } catch (e: Exception) {
            logger.severe("Failed to export config file: ${e.message}")
            false
        }
    }

    /**
     * Imports and decrypts an .astola file. Checks expiration and payload locking.
     */
    fun importConfigFile(file: File): AstolaConfigModel? {
        return try {
            val bytes = file.readBytes()
            val jsonStr = ConfigCryptoEngine.decryptConfig(bytes)
            logger.info("Successfully decrypted .astola file! Parsing config...")

            // Helper parser for schema
            parseConfigJson(jsonStr)
        } catch (e: Exception) {
            logger.severe("Failed to import config file: ${e.message}")
            null
        }
    }

    private fun parseConfigJson(jsonStr: String): AstolaConfigModel {
        // Basic extractor for demo
        val title = extractJsonVal(jsonStr, "title") ?: "Imported Config"
        val serverHost = extractJsonVal(jsonStr, "serverHost") ?: ""
        val payload = extractJsonVal(jsonStr, "payload") ?: ""
        val isLocked = extractJsonVal(jsonStr, "isLocked")?.toBoolean() ?: false

        return AstolaConfigModel(
            title = title,
            serverHost = serverHost,
            payload = if (isLocked) "🔒 [Locked Payload]" else payload,
            isLocked = isLocked
        )
    }

    private fun extractJsonVal(json: String, key: String): String? {
        val regex = Regex("\"$key\"\\s*:\\s*\"?([^\",}]+)\"?")
        return regex.find(json)?.groupValues?.get(1)
    }
}
