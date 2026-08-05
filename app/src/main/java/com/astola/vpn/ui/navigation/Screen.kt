package com.astola.vpn.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Servers : Screen("servers", "Servers", Icons.Default.Dns)
    object Payload : Screen("payload", "Payload", Icons.Default.Code)
    object SplitTunnel : Screen("split_tunnel", "Apps", Icons.Default.Shuffle)
    object Logs : Screen("logs", "Logs", Icons.Default.ListAlt)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)

    companion object {
        val bottomNavItems = listOf(
            Home,
            Servers,
            Payload,
            SplitTunnel,
            Logs,
            Settings
        )
    }
}
