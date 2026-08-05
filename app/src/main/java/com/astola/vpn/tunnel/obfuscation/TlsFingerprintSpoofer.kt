package com.astola.vpn.tunnel.obfuscation

enum class BrowserProfile {
    CHROME,
    FIREFOX,
    SAFARI
}

data class Ja3Profile(
    val browser: BrowserProfile,
    val cipherSuites: List<Int>,
    val extensions: List<Int>,
    val supportedGroups: List<Int>,
    val ecPointFormats: List<Int>
)

object TlsFingerprintSpoofer {

    val ChromeProfile = Ja3Profile(
        browser = BrowserProfile.CHROME,
        cipherSuites = listOf(0x1301, 0x1302, 0x1303, 0xc02b, 0xc02f, 0xcca9, 0xcca8),
        extensions = listOf(0x0000, 0x0017, 0x000d, 0x0005, 0x0012, 0x0010, 0x000b, 0x000a),
        supportedGroups = listOf(0x001d, 0x0017, 0x0018),
        ecPointFormats = listOf(0x00)
    )

    val FirefoxProfile = Ja3Profile(
        browser = BrowserProfile.FIREFOX,
        cipherSuites = listOf(0x1301, 0x1303, 0x1302, 0xc02b, 0xc02f, 0xcca9, 0xcca8),
        extensions = listOf(0x0000, 0x0017, 0x000d, 0x000b, 0x000a, 0x0010),
        supportedGroups = listOf(0x001d, 0x0017, 0x0018, 0x0019),
        ecPointFormats = listOf(0x00)
    )

    fun getProfile(browser: BrowserProfile): Ja3Profile {
        return when (browser) {
            BrowserProfile.CHROME -> ChromeProfile
            BrowserProfile.FIREFOX -> FirefoxProfile
            BrowserProfile.SAFARI -> ChromeProfile
        }
    }
}
