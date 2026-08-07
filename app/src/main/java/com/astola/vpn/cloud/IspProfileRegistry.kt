package com.astola.vpn.cloud

import android.content.Context
import org.json.JSONArray
import java.util.logging.Logger

object IspProfileRegistry {
    private val logger = Logger.getLogger(IspProfileRegistry::class.java.name)
    private var cachedProfiles: List<IspProfile>? = null

    /**
     * Fallback initial profiles in case assets are unreadable before context init.
     */
    private val defaultProfiles = listOf(
        IspProfile(
            id = "whatsapp",
            friendlyName = "WhatsApp Package",
            method = "ssl",
            sniHost = "www.whatsapp.com",
            message = "WhatsApp Package",
            countryFlag = "🌐"
        ),
        IspProfile(
            id = "zong_wa",
            friendlyName = "🇵🇰 Pakistan Zong WhatsApp Pack",
            method = "http",
            payload = "POST http://www.whatsapp.com HTTP/1.0[crlf]Host: http://www.whatsapp.com[crlf]Connection: keep-alive[crlf][crlf]",
            message = "Zong Unlimited WA",
            countryFlag = "🇵🇰"
        ),
        IspProfile(
            id = "zain_y25",
            friendlyName = "🇸🇦 KSA Zain Y25 Pack",
            method = "ssl",
            sniHost = "m.youtube.com",
            message = "YouTube Pack",
            countryFlag = "🇸🇦"
        ),
        IspProfile(
            id = "stc_jawwy_1",
            friendlyName = "🇸🇦 KSA STC Jawwy TV 1",
            method = "http",
            payload = "GET dl.jawwy.tv@[host_port] [protocol][crlf]Host: dl.jawwy.tv[crlf][crlf]",
            message = "STC Jawwy Package",
            countryFlag = "🇸🇦"
        ),
        IspProfile(
            id = "etisalat_social",
            friendlyName = "🇦🇪 Etisalat Social Data",
            method = "http",
            payload = "CONNECT [host_port] [protocol][crlf]Host: lite.facebook.com[crlf][crlf]",
            message = "UAE Social Pack Working",
            countryFlag = "🇦🇪"
        )
    )

    /**
     * Initializes profiles from the app's assets/tweaks.json file.
     */
    fun loadFromAssets(context: Context): List<IspProfile> {
        return try {
            val jsonStr = context.assets.open("tweaks.json").bufferedReader().use { it.readText() }
            val parsed = parseJsonTweaks(jsonStr)
            cachedProfiles = parsed
            logger.info("Successfully loaded ${parsed.size} ISP tweaks from assets/tweaks.json")
            parsed
        } catch (e: Exception) {
            logger.warning("Could not read tweaks.json asset: ${e.message}. Using default profiles.")
            defaultProfiles
        }
    }

    /**
     * Parses raw JSON string into IspProfile list.
     */
    fun parseJsonTweaks(jsonStr: String): List<IspProfile> {
        val list = mutableListOf<IspProfile>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    IspProfile(
                        id = obj.optString("id", "tweak_$i"),
                        friendlyName = obj.optString("friendlyName", "Tweak $i"),
                        category = obj.optString("category", "Free"),
                        method = obj.optString("method", "ssl"),
                        sniHost = obj.optString("sniHost", ""),
                        payload = obj.optString("payload", ""),
                        message = obj.optString("message", ""),
                        countryFlag = obj.optString("countryFlag", "🌐")
                    )
                )
            }
        } catch (e: Exception) {
            logger.severe("Error parsing tweaks JSON: ${e.message}")
        }
        return list
    }

    fun getAllProfiles(context: Context? = null): List<IspProfile> {
        if (context != null && cachedProfiles == null) {
            return loadFromAssets(context)
        }
        return cachedProfiles ?: defaultProfiles
    }

    fun getProfileById(id: String, context: Context? = null): IspProfile? {
        return getAllProfiles(context).find { it.id == id }
    }
}
