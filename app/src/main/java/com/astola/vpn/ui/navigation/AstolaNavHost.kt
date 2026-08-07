package com.astola.vpn.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.astola.vpn.ui.screens.home.HomeScreen
import com.astola.vpn.ui.screens.logs.LogsScreen
import com.astola.vpn.ui.screens.servers.ServerListScreen
import com.astola.vpn.ui.screens.settings.SettingsScreen
import com.astola.vpn.ui.screens.splittunnel.SplitTunnelScreen

@Composable
fun AstolaNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen()
        }
        composable(Screen.Servers.route) {
            ServerListScreen()
        }
        composable(Screen.SplitTunnel.route) {
            SplitTunnelScreen()
        }
        composable(Screen.Logs.route) {
            LogsScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
