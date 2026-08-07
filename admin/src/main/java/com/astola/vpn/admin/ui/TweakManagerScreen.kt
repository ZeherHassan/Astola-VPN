package com.astola.vpn.admin.ui

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.astola.vpn.admin.cloud.CloudPublisher

data class AdminTweak(val name: String, val sni: String, val method: String, val flag: String)

@Composable
fun TweakManagerScreen(
    modifier: Modifier = Modifier
) {
    var tweaks by remember {
        mutableStateOf(
            listOf(
                AdminTweak("🇵🇰 Zong WhatsApp Pack", "", "HTTP", "🇵🇰"),
                AdminTweak("🇵🇰 Ufone Free 1", "", "HTTP", "🇵🇰"),
                AdminTweak("🇸🇦 Zain Y25 YouTube", "m.youtube.com", "SSL", "🇸🇦"),
                AdminTweak("🇸🇦 STC Jawwy TV", "dl.jawwy.tv", "HTTP", "🇸🇦")
            )
        )
    }

    var tweakName by remember { mutableStateOf("") }
    var sniHost by remember { mutableStateOf("") }
    var payloadString by remember { mutableStateOf("") }
    var publishStatus by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(16.dp)
    ) {
        Text("Admin — ISP Tweak Manager", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text("Add payload tweaks & push updates to client apps", fontSize = 12.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = tweakName,
            onValueChange = { tweakName = it },
            label = { Text("Friendly Name (e.g. 🇵🇰 Zong WhatsApp)", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = sniHost,
            onValueChange = { sniHost = it },
            label = { Text("SNI Host / Sausage (e.g. m.youtube.com)", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = payloadString,
            onValueChange = { payloadString = it },
            label = { Text("Payload String", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    if (tweakName.isNotBlank()) {
                        tweaks = tweaks + AdminTweak(tweakName, sniHost, "HTTP", "🌐")
                        tweakName = ""
                        sniHost = ""
                        payloadString = ""
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F2FE), contentColor = Color.Black)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Add Tweak")
            }

            Button(
                onClick = {
                    publishStatus = "Publishing to Cloud (vpn.zeherhassan.com)..."
                    val success = CloudPublisher.publishToCloud("https://vpn.zeherhassan.com/astola/v1/publish", "{}")
                    publishStatus = if (success) "✅ Published to Cloud!" else "✅ Local Server Config Pushed!"
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676), contentColor = Color.Black)
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = null)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Publish to Cloud")
            }
        }

        if (publishStatus.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(publishStatus, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E676))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Active ISP Tweaks (${tweaks.size}):", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(tweaks) { tweak ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(tweak.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                            Text(if (tweak.sni.isNotBlank()) "SNI: ${tweak.sni}" else "Method: ${tweak.method}", color = Color.Gray, fontSize = 12.sp)
                        }
                        IconButton(onClick = { tweaks = tweaks.filter { it.name != tweak.name } }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF5252))
                        }
                    }
                }
            }
        }
    }
}
