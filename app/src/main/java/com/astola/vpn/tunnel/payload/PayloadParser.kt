package com.astola.vpn.tunnel.payload

data class PayloadChunk(
    val data: ByteArray,
    val delayMs: Long = 0L,
    val waitForResponse: Boolean = false
)

object PayloadParser {

    /**
     * Parses a payload string containing macros and expands them into binary data chunks.
     * Handles [host_port], [host], [port], [crlf], [cr], [lf], [protocol], [netData], [split], [delay_split].
     */
    fun parsePayload(
        rawPayload: String,
        targetHost: String,
        targetPort: Int,
        httpProtocol: String = "HTTP/1.1"
    ): List<PayloadChunk> {
        if (rawPayload.isBlank()) {
            return emptyList()
        }

        // Step 1: Expand scalar macros
        var expanded = rawPayload
            .replace("[host_port]", "$targetHost:$targetPort", ignoreCase = true)
            .replace("[host]", targetHost, ignoreCase = true)
            .replace("[port]", targetPort.toString(), ignoreCase = true)
            .replace("[protocol]", httpProtocol, ignoreCase = true)
            .replace("[crlf]", "\r\n", ignoreCase = true)
            .replace("[cr]", "\r", ignoreCase = true)
            .replace("[lf]", "\n", ignoreCase = true)

        // Step 2: Check for [split] or [delay_split] markers
        val chunks = mutableListOf<PayloadChunk>()
        val splitRegex = Regex("\\[(split|delay_split|netData)\\]", RegexOption.IGNORE_CASE)

        val tokens = expanded.split(splitRegex)
        val matches = splitRegex.findAll(expanded).toList()

        for (i in tokens.indices) {
            val tokenText = tokens[i]
            val hasNetData = tokenText.contains("[netData]", ignoreCase = true)
            val cleanedText = tokenText.replace("[netData]", "", ignoreCase = true)

            var delayMs = 0L
            if (i > 0 && i - 1 < matches.size) {
                val matchVal = matches[i - 1].value.lowercase()
                if (matchVal == "[delay_split]") {
                    delayMs = 200L // 200ms delay between split packets
                }
            }

            if (cleanedText.isNotEmpty()) {
                chunks.add(
                    PayloadChunk(
                        data = cleanedText.toByteArray(Charsets.ISO_8859_1),
                        delayMs = delayMs,
                        waitForResponse = hasNetData
                    )
                )
            }
        }

        return if (chunks.isEmpty() && expanded.isNotEmpty()) {
            listOf(PayloadChunk(data = expanded.toByteArray(Charsets.ISO_8859_1)))
        } else {
            chunks
        }
    }
}
