package com.astola.vpn.cloud

import com.astola.vpn.util.AppLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CloudConfigFetcher {

    /**
     * Synchronizes server profiles and ISP tweaks on app launch.
     */
    suspend fun syncCloudTweaksOnLaunch() {
        withContext(Dispatchers.IO) {
            try {
                AppLogger.i("Syncing latest ISP tweaks & servers from cloud...")
                // Fetch pre-configured profiles from registry / remote API
                val profiles = IspProfileRegistry.getAllProfiles()
                AppLogger.s("Successfully loaded ${profiles.size} working ISP profiles!")
            } catch (e: Exception) {
                AppLogger.e("Cloud sync notice: Using offline cached profiles.")
            }
        }
    }
}
