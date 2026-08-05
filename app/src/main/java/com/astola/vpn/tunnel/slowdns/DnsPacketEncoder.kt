package com.astola.vpn.tunnel.slowdns

import java.util.Base64
import java.util.logging.Logger

object DnsPacketEncoder {
    private val logger = Logger.getLogger(DnsPacketEncoder::class.java.name)

    /**
     * Encodes raw tunnel bytes into a valid DNS subdomain hostname for Port 53 transmission.
     * Example: <encoded_payload>.<nameserver_domain>
     */
    fun encodeToDnsQuery(data: ByteArray, nameServerDomain: String): String {
        val encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(data)
            .lowercase()
            .replace("-", "a")
            .replace("_", "b")

        // Split long encoded payloads into 63-character labels (DNS label limit)
        val chunked = encodedPayload.chunked(63).joinToString(".")
        return "$chunked.$nameServerDomain"
    }

    /**
     * Extracts and decodes tunnel payload data from DNS TXT/CNAME response strings.
     */
    fun decodeDnsResponse(responseRecord: String): ByteArray? {
        return try {
            val cleaned = responseRecord.trim().replace("\"", "")
            Base64.getUrlDecoder().decode(cleaned)
        } catch (e: Exception) {
            logger.fine("Failed to decode DNS response record: ${e.message}")
            null
        }
    }
}
