package com.astola.vpn.tunnel.transport

import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.util.Base64
import java.util.Random
import java.util.logging.Logger

object WebSocketTunnel {
    private val logger = Logger.getLogger(WebSocketTunnel::class.java.name)

    /**
     * Performs a WebSocket Upgrade handshake (ws:// or wss://) over an established socket.
     */
    fun performHandshake(
        socket: Socket,
        path: String = "/",
        hostHeader: String,
        customHeaders: Map<String, String> = emptyMap()
    ): Boolean {
        return try {
            val outputStream: OutputStream = socket.getOutputStream()
            val inputStream: InputStream = socket.getInputStream()

            val randomBytes = ByteArray(16)
            Random().nextBytes(randomBytes)
            val secKey = Base64.getEncoder().encodeToString(randomBytes)

            val requestBuilder = StringBuilder()
            requestBuilder.append("GET $path HTTP/1.1\r\n")
            requestBuilder.append("Host: $hostHeader\r\n")
            requestBuilder.append("Upgrade: websocket\r\n")
            requestBuilder.append("Connection: Upgrade\r\n")
            requestBuilder.append("Sec-WebSocket-Key: $secKey\r\n")
            requestBuilder.append("Sec-WebSocket-Version: 13\r\n")

            for ((key, value) in customHeaders) {
                requestBuilder.append("$key: $value\r\n")
            }
            requestBuilder.append("\r\n")

            logger.info("Sending WebSocket Upgrade Request to $hostHeader...")
            outputStream.write(requestBuilder.toString().toByteArray(Charsets.ISO_8859_1))
            outputStream.flush()

            // Read response
            val buffer = ByteArray(2048)
            val bytesRead = inputStream.read(buffer)
            if (bytesRead > 0) {
                val response = String(buffer, 0, bytesRead, Charsets.ISO_8859_1)
                logger.info("WebSocket Handshake Response: ${response.lines().firstOrNull()}")
                return response.contains("101") || response.contains("Switching Protocols", ignoreCase = true)
            }
            false
        } catch (e: Exception) {
            logger.severe("WebSocket Handshake failed: ${e.message}")
            false
        }
    }
}
