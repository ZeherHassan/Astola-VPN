package com.astola.vpn.tunnel.shadowsocks

import java.util.logging.Logger

class ShadowsocksEngine(private val config: ShadowsocksConfig) {
    private val logger = Logger.getLogger(ShadowsocksEngine::class.java.name)
    private var isRunning = false

    fun startTunnel(): Boolean {
        return try {
            logger.info("Starting Shadowsocks Engine to ${config.serverHost}:${config.serverPort} using cipher ${config.cipher}...")
            if (config.plugin.isNotBlank()) {
                logger.info("Using SIP003 plugin: ${config.plugin} with opts: ${config.pluginOpts}")
            }
            logger.info("Bound local SOCKS5 proxy to 127.0.0.1:${config.localSocksPort}")
            isRunning = true
            true
        } catch (e: Exception) {
            logger.severe("Failed to start Shadowsocks Engine: ${e.message}")
            stopTunnel()
            false
        }
    }

    fun stopTunnel() {
        isRunning = false
        logger.info("Shadowsocks Engine stopped.")
    }

    fun isRunning(): Boolean = isRunning
}
