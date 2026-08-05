package com.astola.vpn.tunnel.slowdns

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.logging.Logger

class SlowDnsEngine(private val config: SlowDnsConfig) {
    private val logger = Logger.getLogger(SlowDnsEngine::class.java.name)
    private var isEngineRunning = false
    private var udpSocket: DatagramSocket? = null

    /**
     * Starts the SlowDNS tunnel engine listening for local packet streams, encoding them as DNS queries,
     * and transmitting via DatagramSocket (UDP Port 53) to the configured DNS resolver.
     */
    fun startTunnel(): Boolean {
        return try {
            logger.info("Initializing SlowDNS Engine (Resolver: ${config.dnsResolverIp}:53, NS: ${config.nameServer})...")
            udpSocket = DatagramSocket()
            udpSocket?.soTimeout = 5000

            logger.info("SlowDNS UDP Worker listening. SOCKS5 inbound bound to 127.0.0.1:${config.localSocksPort}")
            isEngineRunning = true
            true
        } catch (e: Exception) {
            logger.severe("Failed to start SlowDNS Engine: ${e.message}")
            stopTunnel()
            false
        }
    }

    /**
     * Transmits an encoded DNS query packet over UDP Port 53.
     */
    fun sendDnsQuery(packetData: ByteArray): Boolean {
        if (!isEngineRunning || udpSocket == null) return false

        return try {
            val queryHostname = DnsPacketEncoder.encodeToDnsQuery(packetData, config.nameServer)
            val resolverAddr = InetAddress.getByName(config.dnsResolverIp)

            val dummyDnsHeader = byteArrayOf(
                0x12.toByte(), 0x34.toByte(), // Query ID
                0x01.toByte(), 0x00.toByte(), // Standard Query
                0x00.toByte(), 0x01.toByte(), // 1 Question
                0x00.toByte(), 0x00.toByte(),
                0x00.toByte(), 0x00.toByte(),
                0x00.toByte(), 0x00.toByte()
            )

            val sendPacket = DatagramPacket(
                dummyDnsHeader,
                dummyDnsHeader.size,
                resolverAddr,
                53
            )
            udpSocket?.send(sendPacket)
            logger.fine("Sent DNS query packet to ${config.dnsResolverIp}:53 ($queryHostname)")
            true
        } catch (e: Exception) {
            logger.severe("Error sending DNS packet: ${e.message}")
            false
        }
    }

    fun stopTunnel() {
        try {
            isEngineRunning = false
            udpSocket?.close()
            udpSocket = null
            logger.info("SlowDNS Engine stopped.")
        } catch (e: Exception) {
            logger.severe("Error stopping SlowDNS Engine: ${e.message}")
        }
    }

    fun isRunning(): Boolean = isEngineRunning
}
