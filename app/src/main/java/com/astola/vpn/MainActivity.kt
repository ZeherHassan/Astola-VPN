package com.astola.vpn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.astola.vpn.ui.components.AstolaBottomBar
import com.astola.vpn.ui.navigation.AstolaNavHost
import com.astola.vpn.ui.theme.AppThemeMode
import com.astola.vpn.ui.theme.AstolaVPNTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate()
        enableEdgeToEdge()
        setContent {
            AstolaVPNTheme(themeMode = AppThemeMode.AMOLED) {
                AstolaMainApp()
            }
        }
    }
}

@Composable
fun AstolaMainApp() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AstolaBottomBar(navController = navController)
        }
    ) { innerPadding ->
        AstolaNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
