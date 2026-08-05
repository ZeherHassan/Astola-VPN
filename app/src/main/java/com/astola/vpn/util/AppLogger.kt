package com.astola.vpn.util

import com.astola.vpn.ui.screens.logs.LogEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AppLogger {
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
    val logs: StateFlow<List<LogEntry>> = _logs.asStateFlow()

    fun i(message: String) = log("INFO", message)
    fun d(message: String) = log("DEBUG", message)
    fun s(message: String) = log("SUCCESS", message)
    fun e(message: String) = log("ERROR", message)

    private fun log(level: String, message: String) {
        val entry = LogEntry(
            time = dateFormat.format(Date()),
            level = level,
            message = message
        )
        _logs.value = _logs.value + entry
    }

    fun clear() {
        _logs.value = emptyList()
    }
}
