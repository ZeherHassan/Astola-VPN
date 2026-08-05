package com.astola.vpn.tunnel.transport

enum class TransportType {
    DIRECT,          // Plain TCP socket connection
    HTTP_PROXY,      // HTTP CONNECT proxy injection
    SSL_TLS,         // TLS/SSL socket wrapper with SNI spoofing
    WEBSOCKET_WS,    // Plain WebSocket stream (ws://)
    WEBSOCKET_WSS    // TLS Encrypted WebSocket stream (wss://)
}
