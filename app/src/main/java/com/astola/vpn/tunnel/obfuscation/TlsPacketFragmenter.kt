package com.astola.vpn.tunnel.obfuscation

import java.io.OutputStream
import java.net.Socket
import java.util.logging.Logger

object TlsPacketFragmenter {
    private val logger = Logger.getLogger(TlsPacketFragmenter::class.java.name)

    /**
     * Fragments a TLS Client Hello record into two TCP segments to defeat SNI-matching DPI equipment.
     */
    fun sendFragmentedTlsHello(
        socket: Socket,
        clientHelloBytes: ByteArray,
        splitOffset: Int = 5,
        delayMs: Long = 50L
    ): Boolean {
        return try {
            val outputStream: OutputStream = socket.getOutputStream()

            if (clientHelloBytes.size <= splitOffset) {
                outputStream.write(clientHelloBytes)
                outputStream.flush()
                return true
            }

            // Packet 1: First splitOffset bytes (splits TLS record header before SNI field)
            val packet1 = clientHelloBytes.copyOfRange(0, splitOffset)
            // Packet 2: Remaining Client Hello bytes
            val packet2 = clientHelloBytes.copyOfRange(splitOffset, clientHelloBytes.size)

            logger.info("Sending Fragmented TLS Client Hello: Part 1 (${packet1.size} bytes)")
            outputStream.write(packet1)
            outputStream.flush()

            if (delayMs > 0) {
                Thread.sleep(delayMs)
            }

            logger.info("Sending Fragmented TLS Client Hello: Part 2 (${packet2.size} bytes)")
            outputStream.write(packet2)
            outputStream.flush()

            true
        } catch (e: Exception) {
            logger.severe("Failed to send fragmented TLS Client Hello: ${e.message}")
            false
        }
    }
}
