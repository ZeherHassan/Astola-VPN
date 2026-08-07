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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.astola.vpn.cloud.CloudApiEngine
import com.astola.vpn.cloud.IspProfileRegistry
import com.astola.vpn.config.AstolaConfigModel
import com.astola.vpn.tunnel.VpnManager
import com.astola.vpn.ui.theme.ElectricCyan
import com.astola.vpn.ui.theme.StatusConnected

@Composable
fun ServerListScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val ispProfiles = remember(context) { IspProfileRegistry.getAllProfiles(context) }
    val activeConfig by VpnManager.activeConfig.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Servers & ISP Tweaks (${ispProfiles.size})",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Connected to Local Host IP: ${CloudApiEngine.HARDCODED_SERVER_IP}",
            fontSize = 12.sp,
            color = ElectricCyan,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(ispProfiles) { profile ->
                val isSelected = activeConfig.title == profile.friendlyName
                val selectLambda = {
                    val newConfig = AstolaConfigModel(
                        title = profile.friendlyName,
                        serverHost = CloudApiEngine.HARDCODED_SERVER_IP,
                        serverPort = 8080,
                        protocol = profile.method.uppercase(),
                        payload = profile.payload,
                        sniHost = profile.sniHost
                    )
                    VpnManager.updateConfig(newConfig)
                }

                Card(
                    onClick = selectLambda,
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = selectLambda,
                                colors = RadioButtonDefaults.colors(selectedColor = ElectricCyan)
                            )
                            Text(profile.countryFlag, fontSize = 22.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Column(modifier = Modifier.padding(start = 8.dp)) {
                                Text(
                                    text = profile.friendlyName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (profile.sniHost.isNotBlank()) "SNI: ${profile.sniHost}" else profile.message.ifBlank { "Method: ${profile.method.uppercase()}" },
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Text(
                            text = "⚡ 12 ms",
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
