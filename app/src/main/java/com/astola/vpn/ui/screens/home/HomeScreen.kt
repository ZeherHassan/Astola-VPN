package com.astola.vpn.ui.screens.home

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.astola.vpn.tunnel.VpnManager
import com.astola.vpn.ui.theme.CyberTeal
import com.astola.vpn.ui.theme.ElectricCyan
import com.astola.vpn.ui.theme.SlateBackground
import com.astola.vpn.ui.theme.SlateOutline
import com.astola.vpn.ui.theme.SlateSurfaceVariant
import com.astola.vpn.ui.theme.StatusConnected
import com.astola.vpn.ui.theme.StatusDisconnected
import com.astola.vpn.ui.theme.TextPrimaryDark

enum class VpnStatus {
    DISCONNECTED, CONNECTING, CONNECTED
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val vpnState by VpnManager.vpnState.collectAsState()
    val downloadSpeed by VpnManager.downloadSpeed.collectAsState()
    val uploadSpeed by VpnManager.uploadSpeed.collectAsState()
    val activeConfig by VpnManager.activeConfig.collectAsState()

    // System VPN Permission Activity Launcher
    val vpnPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            VpnManager.startVpnConnection(context)
        }
    }

    fun handleConnectClick() {
        if (vpnState == VpnStatus.CONNECTED || vpnState == VpnStatus.CONNECTING) {
            VpnManager.disconnectVpn(context)
        } else {
            val prepareIntent = VpnManager.checkVpnPermission(context)
            if (prepareIntent != null) {
                vpnPermissionLauncher.launch(prepareIntent)
            } else {
                VpnManager.startVpnConnection(context)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // App Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Astola VPN",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Box(
                modifier = Modifier
                    .background(
                        color = when (vpnState) {
                            VpnStatus.CONNECTED -> StatusConnected.copy(alpha = 0.15f)
                            VpnStatus.CONNECTING -> ElectricCyan.copy(alpha = 0.15f)
                            VpnStatus.DISCONNECTED -> StatusDisconnected.copy(alpha = 0.15f)
                        },
                        shape = RoundedCornerShape(20.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = when (vpnState) {
                            VpnStatus.CONNECTED -> StatusConnected
                            VpnStatus.CONNECTING -> ElectricCyan
                            VpnStatus.DISCONNECTED -> StatusDisconnected
                        },
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = vpnState.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (vpnState) {
                        VpnStatus.CONNECTED -> StatusConnected
                        VpnStatus.CONNECTING -> ElectricCyan
                        VpnStatus.DISCONNECTED -> StatusDisconnected
                    }
                )
            }
        }

        // Center Animated Connect Button
        ConnectButton(
            vpnState = vpnState,
            onClick = { handleConnectClick() }
        )

        // Live Traffic & Server Info Cards
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Speed Meter
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Download",
                            tint = ElectricCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text("Download", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(downloadSpeed, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Upload",
                            tint = CyberTeal,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text("Upload", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(uploadSpeed, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Server Selection Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Dns,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(activeConfig.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("${activeConfig.protocol} | ${activeConfig.serverHost}:${activeConfig.serverPort}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ConnectButton(
    vpnState: VpnStatus,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (vpnState == VpnStatus.CONNECTED) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.scale(pulseScale)
    ) {
        // Outer Glow Ring
        Box(
            modifier = Modifier
                .size(170.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = if (vpnState == VpnStatus.CONNECTED) {
                            listOf(ElectricCyan.copy(alpha = 0.3f), Color.Transparent)
                        } else {
                            listOf(StatusDisconnected.copy(alpha = 0.15f), Color.Transparent)
                        }
                    ),
                    shape = CircleShape
                )
        )

        // Main Button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(130.dp)
                .background(
                    brush = if (vpnState == VpnStatus.CONNECTED) {
                        Brush.linearGradient(listOf(ElectricCyan, CyberTeal))
                    } else {
                        Brush.linearGradient(listOf(SlateSurfaceVariant, SlateOutline))
                    },
                    shape = CircleShape
                )
                .clickable { onClick() }
        ) {
            Icon(
                imageVector = Icons.Default.PowerSettingsNew,
                contentDescription = "Connect",
                tint = if (vpnState == VpnStatus.CONNECTED) SlateBackground else TextPrimaryDark,
                modifier = Modifier.size(54.dp)
            )
        }
    }
}
