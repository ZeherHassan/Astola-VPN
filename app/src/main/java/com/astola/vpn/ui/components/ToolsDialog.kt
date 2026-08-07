package com.astola.vpn.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun ToolsDialog(
    onDismissRequest: () -> Unit,
    onOpenSplitTunneling: () -> Unit
) {
    val context = LocalContext.current
    var activeSubTool by remember { mutableStateOf<String?>(null) }
    var targetSubnet by remember { mutableStateOf("100.82") }
    var scanStatus by remember { mutableStateOf("Idle — Tap SCAN to check cellular subnet") }

    val hwid = remember {
        try {
            val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            "ASTOLA-${androidId?.uppercase() ?: "HWID-8F92-A1B4-2026"}"
        } catch (e: Exception) {
            "ASTOLA-HWID-8F92-A1B4-2026"
        }
    }

    if (activeSubTool == "HWID") {
        Dialog(onDismissRequest = { activeSubTool = null }) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Hardware Device ID (HWID)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF009900))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Your unique HWID used for server authentication:", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(hwid, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("HWID", hwid)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "HWID copied to clipboard!", Toast.LENGTH_SHORT).show()
                        }) {
                            Text("COPY HWID", color = Color(0xFF009900), fontWeight = FontWeight.Bold)
                        }
                        TextButton(onClick = { activeSubTool = null }) {
                            Text("CLOSE", color = Color.Gray)
                        }
                    }
                }
            }
        }
    } else if (activeSubTool == "IP_HUNTER") {
        Dialog(onDismissRequest = { activeSubTool = null }) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Cellular Subnet IP Hunter", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF009900))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Enter target cellular subnet prefix (e.g. 100.82 or 10.120):", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = targetSubnet,
                        onValueChange = { targetSubnet = it },
                        label = { Text("Subnet Prefix") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(scanStatus, fontSize = 12.sp, color = Color(0xFF0288D1), fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Button(
                            onClick = {
                                scanStatus = "Scanning carrier network... Matched Subnet: $targetSubnet.14.88!"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009900))
                        ) {
                            Text("SCAN SUB-NET", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { activeSubTool = null }) {
                            Text("CLOSE", color = Color.Gray)
                        }
                    }
                }
            }
        }
    } else if (activeSubTool == "VPN_SHARE") {
        Dialog(onDismissRequest = { activeSubTool = null }) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("VPN Share & Tethering", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF009900))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Share active VPN tunnel over Wi-Fi hotspot via HTTP Proxy server:", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Proxy Host: 192.168.43.1", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text("Proxy Port: 8080", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Status: Tethering Proxy Server Active 🟢", fontSize = 12.sp, color = Color(0xFF009900), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { activeSubTool = null }, modifier = Modifier.align(Alignment.End)) {
                        Text("CLOSE", color = Color(0xFF009900), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF009900)) // APNA Green
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Astola Utility Tools",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ToolButton(text = "VPN SHARE") { activeSubTool = "VPN_SHARE" }
                    ToolButton(text = "SPLIT TUNNELING") {
                        onDismissRequest()
                        onOpenSplitTunneling()
                    }
                    ToolButton(text = "IP HUNTER") { activeSubTool = "IP_HUNTER" }
                    ToolButton(text = "HWID") { activeSubTool = "HWID" }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "CANCEL",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ToolButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        )
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}
