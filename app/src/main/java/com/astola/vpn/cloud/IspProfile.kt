package com.astola.vpn.cloud

data class IspProfile(
    val id: String,
    val friendlyName: String,
    val category: String = "Free",
    val method: String,            // ssl, sslws, http, direct
    val sniHost: String = "",      // SNI spoof domain (sausage in tweaks.txt)
    val payload: String = "",      // Custom payload string
    val message: String = "",      // User message / instructions
    val countryFlag: String = "🌐"
)
