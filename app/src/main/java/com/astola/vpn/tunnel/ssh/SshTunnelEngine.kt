package com.astola.vpn.tunnel.ssh

import com.astola.vpn.tunnel.payload.PayloadInjector
import com.astola.vpn.tunnel.payload.PayloadParser
import com.astola.vpn.tunnel.transport.SslSocketWrapper
import com.astola.vpn.tunnel.transport.TransportType
import com.astola.vpn.tunnel.transport.WebSocketTunnel
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Proxy
import com.jcraft.jsch.Session
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.util.logging.Logger

class SshTunnelEngine(private val config: SshConfig) {
    private val logger = Logger.getLogger(SshTunnelEngine::class.java.name)
    private var jschSession: Session? = null

    /**
     * Connects to the remote SSH server using the payload injector and transport wrapper,
     * then opens a dynamic local SOCKS5 port forward on 127.0.0.1:localSocksPort.
     */
    fun startTunnel(): Boolean {
        return try {
            logger.info("Starting SSH Tunnel to ${config.host}:${config.port} via ${config.transportType}...")
            val jsch = JSch()

            if (!config.privateKey.isNullOrBlank()) {
                jsch.addIdentity("default_key", config.privateKey.toByteArray(), null, null)
            }

            val session = jsch.getSession(config.username, config.host, config.port)
            if (!config.password.isNullOrBlank()) {
                session.setPassword(config.password)
            }

            session.setConfig("StrictHostKeyChecking", "no")

            // Custom Proxy implementation using raw socket + PayloadInjector + SSL/WS wrappers
            session.setProxy(object : Proxy {
                private var proxySocket: Socket? = null

                override fun connect(socketFactory: com.jcraft.jsch.SocketFactory?, host: String?, port: Int, timeout: Int) {
                    logger.info("Opening custom proxy connection to $host:$port")
                    var rawSocket = Socket(config.host, config.port)

                    // Step 1: Transport layer wrapping
                    rawSocket = when (config.transportType) {
                        TransportType.SSL_TLS -> {
                            SslSocketWrapper.wrapSocket(rawSocket, config.sniHost, config.host, config.port)
                        }
                        TransportType.WEBSOCKET_WSS -> {
                            val sslSocket = SslSocketWrapper.wrapSocket(rawSocket, config.sniHost, config.host, config.port)
                            WebSocketTunnel.performHandshake(sslSocket, "/", config.sniHost.ifBlank { config.host })
                            sslSocket
                        }
                        TransportType.WEBSOCKET_WS -> {
                            WebSocketTunnel.performHandshake(rawSocket, "/", config.host)
                            rawSocket
                        }
                        else -> rawSocket
                    }

                    // Step 2: Inject Payload if configured
                    if (config.payload.isNotBlank()) {
                        val parsedChunks = PayloadParser.parsePayload(config.payload, config.host, config.port)
                        val injected = PayloadInjector.inject(rawSocket, parsedChunks)
                        if (!injected) {
                            throw Exception("Payload Injection failed!")
                        }
                    }

                    proxySocket = rawSocket
                }

                override fun getInputStream(): InputStream = proxySocket!!.getInputStream()
                override fun getOutputStream(): OutputStream = proxySocket!!.getOutputStream()
                override fun getSocket(): Socket = proxySocket!!
                override fun close() {
                    proxySocket?.close()
                }
            })

            session.connect(30000)
            logger.info("SSH Authentication successful!")

            // Bind Local SOCKS5 Dynamic Port Forwarding
            session.setPortForwardingL(config.localSocksPort, "127.0.0.1", 0)
            logger.info("SOCKS5 Dynamic Port Forward bound to 127.0.0.1:${config.localSocksPort}")

            jschSession = session
            true
        } catch (e: Exception) {
            logger.severe("SSH Tunnel Engine failed: ${e.message}")
            stopTunnel()
            false
        }
    }

    fun stopTunnel() {
        try {
            jschSession?.disconnect()
            jschSession = null
            logger.info("SSH Tunnel disconnected.")
        } catch (e: Exception) {
            logger.severe("Error stopping SSH Tunnel: ${e.message}")
        }
    }
}
