package com.astola.vpn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.astola.vpn.ui.components.AstolaBottomBar
import com.astola.vpn.ui.components.ToolsDialog
import com.astola.vpn.ui.navigation.AstolaNavHost
import com.astola.vpn.ui.navigation.Screen
import com.astola.vpn.ui.theme.AppThemeMode
import com.astola.vpn.ui.theme.AstolaVPNTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AstolaVPNTheme(themeMode = AppThemeMode.LIGHT) {
                AstolaMainApp()
            }
        }
    }
}

@Composable
fun AstolaMainApp() {
    val navController = rememberNavController()
    var showToolsDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AstolaBottomBar(
                onOpenTools = { showToolsDialog = true }
            )
        }
    ) { innerPadding ->
        AstolaNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }

    if (showToolsDialog) {
        ToolsDialog(
            onDismissRequest = { showToolsDialog = false },
            onOpenSplitTunneling = {
                navController.navigate(Screen.SplitTunnel.route)
            }
        )
    }
}
