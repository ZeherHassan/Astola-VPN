package com.astola.vpn.ui.screens.settings

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    var dnsOverHttps by remember { mutableStateOf(true) }
    var amoledTheme by remember { mutableStateOf(true) }
    var autoReconnect by remember { mutableStateOf(true) }
    var ipv6Bypass by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "App preferences, networking & security options",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                SettingToggleItem(
                    title = "DNS-over-HTTPS (DoH)",
                    subtitle = "Encrypt DNS queries via Cloudflare (1.1.1.1) to prevent ISP DNS leaks",
                    checked = dnsOverHttps,
                    onCheckedChange = { dnsOverHttps = it }
                )
            }
            item {
                SettingToggleItem(
                    title = "AMOLED Black Theme",
                    subtitle = "Use pitch black backgrounds to conserve OLED display battery",
                    checked = amoledTheme,
                    onCheckedChange = { amoledTheme = it }
                )
            }
            item {
                SettingToggleItem(
                    title = "Auto-Reconnect on Drop",
                    subtitle = "Automatically re-establish tunnel when network connection changes",
                    checked = autoReconnect,
                    onCheckedChange = { autoReconnect = it }
                )
            }
            item {
                SettingToggleItem(
                    title = "Block IPv6 Leak",
                    subtitle = "Drop IPv6 packets to prevent location leaks outside tunnel",
                    checked = ipv6Bypass,
                    onCheckedChange = { ipv6Bypass = it }
                )
            }
        }
    }
}

@Composable
fun SettingToggleItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = ElectricCyan,
                    checkedTrackColor = ElectricCyan.copy(alpha = 0.3f)
                )
            )
        }
    }
}
