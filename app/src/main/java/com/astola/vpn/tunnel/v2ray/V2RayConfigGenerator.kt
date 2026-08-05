package com.astola.vpn.tunnel.v2ray

import com.astola.vpn.tunnel.v2ray.model.V2RayConfig
import com.astola.vpn.tunnel.v2ray.model.V2RayNetwork
import com.astola.vpn.tunnel.v2ray.model.V2RayProtocol
import com.astola.vpn.tunnel.v2ray.model.V2RaySecurity

object V2RayConfigGenerator {

    /**
     * Generates a complete, valid V2Ray / Xray JSON configuration string from a V2RayConfig object.
     */
    fun generateJson(config: V2RayConfig): String {
        val streamSettingsJson = buildStreamSettings(config)
        val outboundProtocolJson = buildOutboundProtocol(config)

        return """
        {
          "log": {
            "loglevel": "warning"
          },
          "inbounds": [
            {
              "port": ${config.localSocksPort},
              "listen": "127.0.0.1",
              "protocol": "socks",
              "settings": {
                "auth": "noauth",
                "udp": true
              }
            }
          ],
          "outbounds": [
            {
              "protocol": "${config.protocol.name.lowercase()}",
              "settings": {
                $outboundProtocolJson
              },
              "streamSettings": $streamSettingsJson,
              "tag": "proxy"
            },
            {
              "protocol": "freedom",
              "tag": "direct"
            }
          ],
          "dns": {
            "servers": [
              "1.1.1.1",
              "8.8.8.8"
            ]
          }
        }
        """.trimIndent()
    }

    private fun buildOutboundProtocol(config: V2RayConfig): String {
        return when (config.protocol) {
            V2RayProtocol.VMESS -> """
              "vnext": [
                {
                  "address": "${config.address}",
                  "port": ${config.port},
                  "users": [
                    {
                      "id": "${config.uuidOrPassword}",
                      "alterId": ${config.alterId},
                      "security": "auto"
                    }
                  ]
                }
              ]
            """.trimIndent()

            V2RayProtocol.VLESS -> """
              "vnext": [
                {
                  "address": "${config.address}",
                  "port": ${config.port},
                  "users": [
                    {
                      "id": "${config.uuidOrPassword}",
                      "encryption": "none",
                      "flow": "${config.flow}"
                    }
                  ]
                }
              ]
            """.trimIndent()

            V2RayProtocol.TROJAN -> """
              "servers": [
                {
                  "address": "${config.address}",
                  "port": ${config.port},
                  "password": "${config.uuidOrPassword}"
                }
              ]
            """.trimIndent()
        }
    }

    private fun buildStreamSettings(config: V2RayConfig): String {
        val net = config.network.name.lowercase()
        val security = config.security.name.lowercase()

        val transportSettings = when (config.network) {
            V2RayNetwork.WS -> """
              "wsSettings": {
                "path": "${config.path}",
                "headers": {
                  "Host": "${config.hostHeader.ifBlank { config.sni }}"
                }
              },
            """.trimIndent()

            V2RayNetwork.GRPC -> """
              "grpcSettings": {
                "serviceName": "${config.serviceName}"
              },
            """.trimIndent()

            V2RayNetwork.H2 -> """
              "httpSettings": {
                "host": ["${config.hostHeader.ifBlank { config.sni }}"],
                "path": "${config.path}"
              },
            """.trimIndent()

            else -> ""
        }

        val securitySettings = when (config.security) {
            V2RaySecurity.TLS -> """
              "tlsSettings": {
                "serverName": "${config.sni.ifBlank { config.address }}",
                "allowInsecure": ${config.allowInsecure},
                "fingerprint": "${config.fingerprint}"
              }
            """.trimIndent()

            V2RaySecurity.REALITY -> """
              "realitySettings": {
                "serverName": "${config.sni.ifBlank { config.address }}",
                "fingerprint": "${config.fingerprint}",
                "publicKey": "${config.publicKey}",
                "shortId": "${config.shortId}",
                "spiderX": "${config.spiderX}"
              }
            """.trimIndent()

            else -> ""
        }

        return """
        {
          "network": "$net",
          "security": "$security",
          $transportSettings
          $securitySettings
        }
        """.trimIndent()
    }
}
