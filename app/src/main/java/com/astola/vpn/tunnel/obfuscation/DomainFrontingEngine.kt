package com.astola.vpn.tunnel.obfuscation

data class FrontingConfig(
    val frontDomain: String,       // Allowed SNI domain (e.g. cdn.cloudflare.com)
    val targetHost: String,        // Real VPN target server domain/IP
    val targetPort: Int = 443
)

object DomainFrontingEngine {

    /**
     * Constructs HTTP CONNECT headers for CDN Domain Fronting.
     * SNI will see `frontDomain`, while the CDN edge worker forwards to `targetHost`.
     */
    fun buildFrontingConnectHeader(config: FrontingConfig): String {
        return StringBuilder().apply {
            append("CONNECT ${config.targetHost}:${config.targetPort} HTTP/1.1\r\n")
            append("Host: ${config.frontDomain}\r\n")
            append("X-Forwarded-Host: ${config.targetHost}\r\n")
            append("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36\r\n")
            append("Connection: Keep-Alive\r\n")
            append("\r\n")
        }.toString()
    }
}
