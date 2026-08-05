# Astola VPN — ProGuard & R8 Obfuscation Rules

# Keep Jetpack Compose & Material 3 rules
-keep class androidx.compose.** { *; }
-keepclassmembers class * extends androidx.compose.ui.Modifier { *; }

# Keep JSch SSH Tunnel Library Symbols
-keep class com.jcraft.jsch.** { *; }
-keep interface com.jcraft.jsch.** { *; }

# Keep V2Ray / Xray & JSON Data Models
-keep class com.astola.vpn.tunnel.v2ray.model.** { *; }
-keep class com.astola.vpn.config.AstolaConfigModel { *; }
-keep class com.astola.vpn.cloud.IspProfile { *; }

# Obfuscate internal payload parser & crypto algorithms
-repackageclasses 'com.astola.vpn.a'
-allowaccessmodification
-dontusemixedcaseclassnames
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# Keep Android VpnService and Foreground Service
-keep class * extends android.net.VpnService
-keep class com.astola.vpn.tunnel.vpn.AstolaVpnService { *; }
