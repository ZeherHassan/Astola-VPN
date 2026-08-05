package com.astola.vpn.tunnel.shadowsocks

enum class ShadowsocksCipher {
    CHACHA20_IETF_POLY1305,
    AES_256_GCM,
    AES_128_GCM
}

data class ShadowsocksConfig(
    val serverHost: String,
    val serverPort: Int = 8388,
    val password: String,
    val cipher: ShadowsocksCipher = ShadowsocksCipher.CHACHA20_IETF_POLY1305,
    val plugin: String = "",           // e.g., obfs-local, v2ray-plugin
    val pluginOpts: String = "",       // e.g., obfs=http;obfs-host=free.facebook.com
    val localSocksPort: Int = 1080
)
