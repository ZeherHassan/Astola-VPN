package com.astola.vpn.ui.screens.servers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.astola.vpn.ui.theme.ElectricCyan
import com.astola.vpn.ui.theme.StatusConnected

import androidx.compose.runtime.LaunchedEffect
import com.astola.vpn.util.PingUtil

data class ServerItem(
    val id: String,
    val flag: String,
    val name: String,
    val protocol: String,
    val host: String,
    val port: Int,
    val pingMs: Int
)

@Composable
fun ServerListScreen(
    modifier: Modifier = Modifier
) {
    var servers by remember {
        mutableStateOf(
            listOf(
                ServerItem("1", "🇵🇰", "Pakistan — ISB #1", "SSH | SSL-WebSocket", "185.220.101.5", 443, 35),
                ServerItem("2", "🇵🇰", "Pakistan — KHI #2", "SSH | Direct Payload", "185.220.101.6", 80, 42),
                ServerItem("3", "🇸🇬", "Singapore — SG-1", "V2Ray | VMess WS", "139.99.12.4", 443, 78),
                ServerItem("4", "🇩🇪", "Germany — FRA #1", "Xray | VLESS REALITY", "51.15.220.1", 443, 120),
                ServerItem("5", "🇺🇸", "USA — NYC #1", "Shadowsocks | AEAD", "104.238.150.2", 8388, 190)
            )
        )
    }

    LaunchedEffect(Unit) {
        servers = servers.map { server ->
            val ping = PingUtil.pingHost(server.host, server.port)
            if (ping > 0) server.copy(pingMs = ping) else server
        }
    }

    var selectedId by remember { mutableStateOf("1") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Select Server",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Choose your tunnel server and region profile",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(servers) { server ->
                val isSelected = server.id == selectedId
                Card(
                    onClick = { selectedId = server.id },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedId = server.id },
                                colors = RadioButtonDefaults.colors(selectedColor = ElectricCyan)
                            )
                            Text(server.flag, fontSize = 24.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Column(modifier = Modifier.padding(start = 8.dp)) {
                                Text(server.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(server.protocol, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Text(
                            text = "⚡ ${server.pingMs} ms",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusConnected
                        )
                    }
                }
            }
        }
    }
}
