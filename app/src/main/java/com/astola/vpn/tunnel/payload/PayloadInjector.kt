package com.astola.vpn.tunnel.payload

import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.util.logging.Logger

object PayloadInjector {
    private val logger = Logger.getLogger(PayloadInjector::class.java.name)

    /**
     * Injects the parsed payload chunks into the output stream of an open TCP socket.
     */
    fun inject(
        socket: Socket,
        chunks: List<PayloadChunk>
    ): Boolean {
        if (chunks.isEmpty()) return true

        return try {
            val outputStream: OutputStream = socket.getOutputStream()
            val inputStream: InputStream = socket.getInputStream()

            for ((index, chunk) in chunks.withIndex()) {
                if (chunk.delayMs > 0) {
                    logger.info("Delaying split packet #${index + 1} for ${chunk.delayMs}ms")
                    Thread.sleep(chunk.delayMs)
                }

                logger.info("Sending payload chunk #${index + 1} (${chunk.data.size} bytes)")
                outputStream.write(chunk.data)
                outputStream.flush()

                if (chunk.waitForResponse) {
                    logger.info("Waiting for proxy/ISP [netData] response...")
                    val buffer = ByteArray(1024)
                    val read = inputStream.read(buffer)
                    if (read > 0) {
                        val responseStr = String(buffer, 0, read, Charsets.ISO_8859_1)
                        logger.info("Received [netData] response: ${responseStr.take(100)}")
                    }
                }
            }
            true
        } catch (e: Exception) {
            logger.severe("Payload injection failed: ${e.message}")
            false
        }
    }
}
