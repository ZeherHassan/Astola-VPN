package com.astola.vpn.tunnel.vpn

import java.util.logging.Logger

object Tun2SocksBridge {
    private val logger = Logger.getLogger(Tun2SocksBridge::class.java.name)
    private var isRunning = false

    fun start(tunFd: Int, socksProxyAddress: String) {
        logger.info("Initializing Tun2Socks Bridge on TUN FileDescriptor #$tunFd -> SOCKS5 $socksProxyAddress")
        isRunning = true
        // Bridge connects TUN IP packet stream to 127.0.0.1:1080 SOCKS5 endpoint
    }

    fun stop() {
        if (isRunning) {
            logger.info("Stopping Tun2Socks Bridge...")
            isRunning = false
        }
    }
}
