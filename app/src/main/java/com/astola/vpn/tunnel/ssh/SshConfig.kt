package com.astola.vpn.tunnel.ssh

import com.astola.vpn.tunnel.transport.TransportType

data class SshConfig(
    val host: String,
    val port: Int = 22,
    val username: String,
    val password: String? = null,
    val privateKey: String? = null,
    val payload: String = "",
    val sniHost: String = "",
    val transportType: TransportType = TransportType.DIRECT,
    val localSocksPort: Int = 1080
)
