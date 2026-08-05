package com.astola.vpn.tunnel.v2ray.model

data class V2RayConfig(
    val remarks: String = "V2Ray Profile",
    val address: String,
    val port: Int = 443,
    val uuidOrPassword: String,
    val alterId: Int = 0,               // For VMess
    val flow: String = "",              // For VLESS (e.g. xtls-rprx-vision)
    val protocol: V2RayProtocol = V2RayProtocol.VMESS,
    val network: V2RayNetwork = V2RayNetwork.WS,
    val path: String = "/",             // For WS / H2 path
    val serviceName: String = "",       // For gRPC service name
    val hostHeader: String = "",        // HTTP Host Header
    val security: V2RaySecurity = V2RaySecurity.TLS,
    val sni: String = "",               // Server Name Indication
    val fingerprint: String = "chrome", // TLS Fingerprint (chrome, firefox, safari)
    val publicKey: String = "",         // For REALITY
    val shortId: String = "",           // For REALITY
    val spiderX: String = "/",          // For REALITY
    val allowInsecure: Boolean = false,
    val localSocksPort: Int = 10808
)
