package com.astola.vpn.config

data class AstolaConfigModel(
    val configId: String = java.util.UUID.randomUUID().toString(),
    val title: String = "Untitled Config",
    val description: String = "",
    val serverHost: String = "",
    val serverPort: Int = 443,
    val protocol: String = "SSH", // SSH, VMESS, VLESS, TROJAN, SHADOWSOCKS, SLOWDNS
    val username: String = "",
    val password: String = "",
    val payload: String = "",
    val sniHost: String = "",
    val isLocked: Boolean = false,        // If true, hide payload and server edit fields from user
    val expiryTimestamp: Long = 0L,        // 0 = no expiry
    val createdAt: Long = System.currentTimeMillis()
)
