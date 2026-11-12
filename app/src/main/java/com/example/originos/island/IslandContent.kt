package com.example.originos.island

import androidx.compose.ui.graphics.Color

/**
 * The live activity currently hosted by the Origin Island.
 */
sealed interface IslandContent {

    data object Idle : IslandContent

    data class Music(
        val title: String,
        val artist: String,
        val playing: Boolean,
        val progress: Float,
    ) : IslandContent

    data class Timer(
        val remaining: Int,
        val total: Int,
        val running: Boolean,
    ) : IslandContent

    data class Call(
        val name: String,
        val connected: Boolean = false,
        val seconds: Int = 0,
    ) : IslandContent

    data class Charging(val percent: Int) : IslandContent
}

enum class Accent(val color: Color, val label: String) {
    Blue(Color(0xFF5B8CFF), "Blue"),
    Violet(Color(0xFFB18CFF), "Violet"),
    Green(Color(0xFF3DDC97), "Green"),
    Orange(Color(0xFFFF9F45), "Orange"),
}

fun formatDuration(totalSeconds: Int): String {
    val safe = totalSeconds.coerceAtLeast(0)
    val minutes = safe / 60
    val seconds = safe % 60
    return "%02d:%02d".format(minutes, seconds)
}
