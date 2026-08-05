package com.astola.vpn.cloud

object IspProfileRegistry {

    private val profiles = listOf(
        IspProfile(
            id = "zong_wa",
            friendlyName = "🇵🇰 Pakistan Zong WhatsApp Pack",
            method = "http",
            payload = "POST http://www.whatsapp.com HTTP/1.0[crlf]Host: http://www.whatsapp.com[crlf]Connection: keep-alive[crlf]X-Online-Host: http://www.whatsapp.com[crlf]X-Forward-Host: http://www.whatsapp.com[crlf][crlf]",
            message = "If no connect, toggle flight mode ✈️",
            countryFlag = "🇵🇰"
        ),
        IspProfile(
            id = "ufone_free_1",
            friendlyName = "🇵🇰 Pakistan Ufone Free 1",
            method = "http",
            payload = "CONNECT [host_port] HTTP/1.0[crlf]Host: ufonepk.portal.ncn.mobi.com[crlf][crlf]",
            message = "Ufone Free Pack 1",
            countryFlag = "🇵🇰"
        ),
        IspProfile(
            id = "ufone_free_2",
            friendlyName = "🇵🇰 Pakistan Ufone Free 2",
            method = "http",
            payload = "GET / HTTP/1.1[crlf]Host: ufonepk.filter.ncnd.mobi/nc[crlf]Upgrade: websocket[crlf][crlf]",
            message = "Ufone Free Pack 2",
            countryFlag = "🇵🇰"
        ),
        IspProfile(
            id = "scom_wa",
            friendlyName = "🇵🇰 AJK SCOM WhatsApp Pack",
            method = "http",
            payload = "POST http://www.whatsapp.com HTTP/1.0[crlf]Host: http://www.whatsapp.com[crlf]Connection: keep-alive[crlf][crlf]",
            message = "Azad Kashmir SCOM",
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
            id = "zain_60r",
            friendlyName = "🇸🇦 KSA Zain 60 Riyals",
            method = "ssl",
            sniHost = "www.snapchat.com",
            message = "312 send 959",
            countryFlag = "🇸🇦"
        ),
        IspProfile(
            id = "stc_jawwy_1",
            friendlyName = "🇸🇦 KSA STC Jawwy TV 1",
            method = "http",
            payload = "GET dl.jawwy.tv@[host_port] [protocol][crlf]Host: dl.jawwy.tv[crlf]X-Online-Host: dl.jawwy.tv[crlf][crlf]",
            message = "STC Jawwy Package",
            countryFlag = "🇸🇦"
        ),
        IspProfile(
            id = "mobily_free_x",
            friendlyName = "🇸🇦 KSA Mobily Free X",
            method = "http",
            payload = "CONNECT [host_port]@outcome-ssp.supersonicads.com [protocol][crlf]Host: outcome-ssp.supersonicads.com[crlf]Connection: Keep-Alive[crlf][crlf]",
            message = "Mobily Unblock Area",
            countryFlag = "🇸🇦"
        ),
        IspProfile(
            id = "etisalat_social",
            friendlyName = "🇦🇪 Etisalat Social Data",
            method = "http",
            payload = "CONNECT [host_port] [protocol][crlf]Host: lite.facebook.com[crlf]X-Online-Host: lite.facebook.com[crlf]Connection: Keep-Alive[crlf][crlf]",
            message = "UAE Social Pack Working",
            countryFlag = "🇦🇪"
        ),
        IspProfile(
            id = "dhiraagu_free",
            friendlyName = "🇲🇻 Maldives Dhiraagu Free",
            method = "http",
            payload = "POST http://dhiraagu.com.mv/ HTTP/1.1[crlf]Host: dhiraagu.com.mv[crlf][crlf]CONNECT [host_port] [protocol][crlf][crlf]",
            message = "Maldives Dhiraagu",
            countryFlag = "🇲🇻"
        )
    )

    fun getAllProfiles(): List<IspProfile> = profiles

    fun getProfileById(id: String): IspProfile? {
        return profiles.find { it.id == id }
    }
}
