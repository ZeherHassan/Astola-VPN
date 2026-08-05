package com.astola.vpn.tunnel.transport

import java.net.InetAddress
import java.net.Socket
import javax.net.ssl.SNIHostName
import javax.net.ssl.SSLParameters
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

object SslSocketWrapper {

    /**
     * Wraps a connected raw TCP socket inside an SSL/TLS socket with custom SNI host spoofing.
     */
    fun wrapSocket(
        plainSocket: Socket,
        sniHost: String,
        targetHost: String,
        targetPort: Int,
        allowInsecure: Boolean = true
    ): SSLSocket {
        val sslFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
        val sslSocket = sslFactory.createSocket(
            plainSocket,
            targetHost,
            targetPort,
            true
        ) as SSLSocket

        // Inject custom SNI (Server Name Indication) domain for spoofing
        val sslParams = sslSocket.sslParameters ?: SSLParameters()
        if (sniHost.isNotBlank()) {
            sslParams.serverNames = listOf(SNIHostName(sniHost))
        }
        sslSocket.sslParameters = sslParams

        sslSocket.startHandshake()
        return sslSocket
    }
}
