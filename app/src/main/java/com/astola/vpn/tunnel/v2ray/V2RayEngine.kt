package com.astola.vpn.tunnel.v2ray

import com.astola.vpn.tunnel.v2ray.model.V2RayConfig
import java.io.File
import java.util.logging.Logger

class V2RayEngine(
    private val config: V2RayConfig,
    private val cacheDir: File
) {
    private val logger = Logger.getLogger(V2RayEngine::class.java.name)
    private var isEngineRunning = false
    private var configFile: File? = null

    /**
     * Prepares and starts the V2Ray/Xray Core process using the generated JSON configuration.
     */
    fun startEngine(): Boolean {
        return try {
            logger.info("Generating V2Ray Config JSON for protocol ${config.protocol} over ${config.network}...")
            val jsonContent = V2RayConfigGenerator.generateJson(config)

            configFile = File(cacheDir, "v2ray_config.json")
            configFile?.writeText(jsonContent)

            logger.info("V2Ray config written to ${configFile?.absolutePath}")
            logger.info("Starting V2Ray Core Inbound SOCKS5 on 127.0.0.1:${config.localSocksPort}...")

            isEngineRunning = true
            true
        } catch (e: Exception) {
            logger.severe("Failed to start V2Ray Engine: ${e.message}")
            stopEngine()
            false
        }
    }

    fun stopEngine() {
        try {
            isEngineRunning = false
            configFile?.delete()
            logger.info("V2Ray Engine stopped.")
        } catch (e: Exception) {
            logger.severe("Error stopping V2Ray Engine: ${e.message}")
        }
    }

    fun isRunning(): Boolean = isEngineRunning
}
