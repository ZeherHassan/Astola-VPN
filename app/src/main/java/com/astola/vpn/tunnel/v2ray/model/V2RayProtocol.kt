package com.astola.vpn.tunnel.v2ray.model

enum class V2RayProtocol {
    VMESS,
    VLESS,
    TROJAN
}

enum class V2RayNetwork {
    TCP,
    WS,      // WebSocket
    GRPC,    // gRPC
    H2       // HTTP/2
}

enum class V2RaySecurity {
    NONE,
    TLS,
    REALITY
}
