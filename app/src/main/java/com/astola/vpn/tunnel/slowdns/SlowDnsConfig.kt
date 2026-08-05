package com.astola.vpn.tunnel.slowdns

data class SlowDnsConfig(
    val nameServer: String,              // Domain name server (e.g. ns.yourdomain.com)
    val publicKey: String = "",          // Encryption public key for SlowDNS
    val dnsResolverIp: String = "8.8.8.8",// Public DNS resolver IP (UDP Port 53)
    val queryType: String = "TXT",       // TXT or CNAME record type
    val localSocksPort: Int = 1080
)
