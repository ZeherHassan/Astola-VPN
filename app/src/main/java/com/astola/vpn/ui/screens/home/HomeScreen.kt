package com.astola.vpn.ui.screens.home

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.astola.vpn.cloud.IspProfile
import com.astola.vpn.cloud.IspProfileRegistry
import com.astola.vpn.config.AstolaConfigModel
import com.astola.vpn.tunnel.VpnManager
import com.astola.vpn.ui.components.ServerOption
import com.astola.vpn.ui.components.ServerSelectionDialog
import com.astola.vpn.ui.components.TweakSelectionDialog

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
    val sessionDuration by VpnManager.sessionDuration.collectAsState()
    val activeConfig by VpnManager.activeConfig.collectAsState()

    var showServerDialog by remember { mutableStateOf(false) }
    var showTweakDialog by remember { mutableStateOf(false) }
    var selectedServerOption by remember {
        mutableStateOf(ServerOption("auto", "Auto Select Server", "🛡️", "vpn.zeherhassan.com", 443))
    }
    var selectedTweakOption by remember {
        mutableStateOf(
            IspProfileRegistry.getProfileById("zong_wa")
                ?: IspProfile("wa", "🇵🇰 Pakistan Zong WhatsApp Pack", "Free", "http", payload = "POST http://www.whatsapp.com...")
        )
    }
    var customDnsChecked by remember { mutableStateOf(true) }

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
            .background(Color(0xFFF2F2F2)), // Light APNA Gray
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Title Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Astola VPN",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF009900) // APNA Green
            )
        }

        // World Map & Status Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Astola Build - 609", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.align(Alignment.Start))

            Spacer(modifier = Modifier.height(4.dp))

            // Logo Badge in Center
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = Color(0xFF00E676),
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Live Counters Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Color(0xFF009900), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(downloadSpeed, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }

                Text(
                    text = if (selectedTweakOption.method.lowercase() == "ssl") "SSL/TLS" else selectedTweakOption.method.uppercase(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(uploadSpeed, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color(0xFF009900), modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Connection Status Label
            Text(
                text = if (vpnState == VpnStatus.CONNECTED) "Connected" else if (vpnState == VpnStatus.CONNECTING) "Connecting..." else "Disconnected",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (vpnState == VpnStatus.CONNECTED) Color(0xFF009900) else Color(0xFFD32F2F)
            )

            Text(
                text = "Config Version : 609  |  VPN Duration : $sessionDuration",
                fontSize = 11.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Pill 1: Server Selector Dropdown
        PillDropdown(
            leftIcon = Icons.Default.LocationOn,
            title = selectedServerOption.name,
            subtitle = "Astola VPN",
            rightBadge = "Random",
            onClick = { showServerDialog = true }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Pill 2: ISP Tweak Selector Dropdown
        PillDropdown(
            leftIcon = Icons.Default.Language,
            title = selectedTweakOption.friendlyName,
            subtitle = "Astola VPN",
            rightBadge = "",
            isTweak = true,
            onClick = { showTweakDialog = true }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Checkbox: DNS Custom
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = customDnsChecked,
                onCheckedChange = { customDnsChecked = it },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF009900))
            )
            Text("DNS (Custom)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }

        Spacer(modifier = Modifier.weight(1f))

        // START / STOP Button Container (White Arch)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(Color.White, shape = RoundedCornerShape(topStart = 100.dp, topEnd = 100.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Circular Start Button
            Box(
                modifier = Modifier
                    .size(105.dp)
                    .clip(CircleShape)
                    .border(4.dp, Color(0xFFCCCCCC), CircleShape)
                    .border(6.dp, Color(0xFF009900), CircleShape)
                    .clickable { handleConnectClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (vpnState == VpnStatus.CONNECTED) "STOP" else "START",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (vpnState == VpnStatus.CONNECTED) Color(0xFFD32F2F) else Color(0xFFB71C1C)
                )
            }
        }

        // Google AdMob Banner Placement Slot
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color(0xFFE0E0E0))
                .border(1.dp, Color(0xFFBDBDBD)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📢 Google AdMob Banner Slot",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
        }
    }

    // Modal Dialogs
    if (showServerDialog) {
        ServerSelectionDialog(
            currentSelectedId = selectedServerOption.id,
            onDismissRequest = { showServerDialog = false },
            onServerSelected = { option ->
                selectedServerOption = option
                VpnManager.updateConfig(
                    AstolaConfigModel(
                        title = option.name,
                        serverHost = option.host,
                        serverPort = option.port,
                        protocol = selectedTweakOption.method.uppercase(),
                        payload = selectedTweakOption.payload,
                        sniHost = selectedTweakOption.sniHost
                    )
                )
            }
        )
    }

    if (showTweakDialog) {
        TweakSelectionDialog(
            currentSelectedId = selectedTweakOption.id,
            onDismissRequest = { showTweakDialog = false },
            onTweakSelected = { tweak ->
                selectedTweakOption = tweak
                VpnManager.updateConfig(
                    AstolaConfigModel(
                        title = tweak.friendlyName,
                        serverHost = selectedServerOption.host,
                        serverPort = selectedServerOption.port,
                        protocol = tweak.method.uppercase(),
                        payload = tweak.payload,
                        sniHost = tweak.sniHost
                    )
                )
            }
        )
    }
}

@Composable
fun PillDropdown(
    leftIcon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    rightBadge: String,
    isTweak: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(58.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(Color.White)
            .border(2.dp, Color(0xFF009900), RoundedCornerShape(30.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color(0xFF009900), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(leftIcon, contentDescription = null, tint = Color(0xFF009900), modifier = Modifier.size(20.dp))
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black)
                    Text(subtitle, fontSize = 10.sp, color = Color(0xFF009900), fontWeight = FontWeight.SemiBold)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (rightBadge.isNotBlank()) {
                    Text(rightBadge, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF000080))
                    Spacer(modifier = Modifier.width(4.dp))
                }
                if (isTweak) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF0288D1), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Black)
            }
        }
    }
}
