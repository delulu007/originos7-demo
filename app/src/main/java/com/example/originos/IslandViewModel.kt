package com.example.originos

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.originos.island.Accent
import com.example.originos.island.IslandContent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private data class Track(val title: String, val artist: String)

/**
 * Drives the demo: island content, expansion, simulated media/timer/call ticks.
 * Everything is local and fake on purpose — no notification or media permissions needed.
 */
class IslandViewModel : ViewModel() {

    var content by mutableStateOf<IslandContent>(IslandContent.Idle)
        private set

    var expanded by mutableStateOf(false)
        private set

    var accent by mutableStateOf(Accent.Blue)
        private set

    var panelOpen by mutableStateOf(false)
        private set

    var locked by mutableStateOf(true)
        private set

    var controlCenterOpen by mutableStateOf(false)
        private set

    var brightness by mutableFloatStateOf(0.72f)
        private set

    var volume by mutableFloatStateOf(0.45f)
        private set

    var enabledToggles by mutableStateOf(setOf("wifi", "mobile", "rotate"))
        private set

    private var ticker: Job? = null
    private var autoExpand: Job? = null

    private val playlist = listOf(
        Track("Midnight City", "M83"),
        Track("Sunset Drive", "The Midnight"),
        Track("Neon Bloom", "Polaris"),
        Track("Afterglow", "Tycho"),
    )
    private var trackIndex = 0

    /** Show a new activity compactly, then expand it shortly after to show off the morph. */
    fun launch(next: IslandContent) {
        panelOpen = false
        ticker?.cancel()
        autoExpand?.cancel()
        content = next
        expanded = false

        when (next) {
            is IslandContent.Idle -> Unit
            is IslandContent.Music -> if (next.playing) startMusicTicker()
            is IslandContent.Timer -> startTimerTicker()
            is IslandContent.Charging -> startChargingTicker()
            is IslandContent.Call -> if (next.connected) startCallTicker()
            is IslandContent.Message -> Unit
        }

        if (next !is IslandContent.Idle) {
            autoExpand = viewModelScope.launch {
                delay(550)
                expanded = true
            }
        }
    }

    fun playMusic(index: Int = trackIndex) {
        trackIndex = index.coerceIn(playlist.indices)
        val track = playlist[trackIndex]
        launch(
            IslandContent.Music(
                title = track.title,
                artist = track.artist,
                playing = true,
                progress = 0.18f,
            ),
        )
    }

    fun startTimer() {
        launch(IslandContent.Timer(remaining = 5 * 60, total = 5 * 60, running = true))
    }

    fun incomingCall() {
        launch(IslandContent.Call(name = "Maya Sharma", connected = false))
    }

    fun charging() {
        launch(IslandContent.Charging(percent = 63))
    }

    fun incomingMessage() {
        launch(
            IslandContent.Message(
                sender = "Maya Sharma",
                text = "Are we still on for dinner at 8?",
            ),
        )
    }

    fun replyToMessage() {
        val message = content as? IslandContent.Message ?: return
        content = message.copy(replied = true)
    }

    fun dismiss() {
        ticker?.cancel()
        autoExpand?.cancel()
        content = IslandContent.Idle
        expanded = false
    }

    fun toggleExpanded() {
        if (content is IslandContent.Idle) {
            panelOpen = true
            return
        }
        expanded = !expanded
    }

    fun collapse() {
        expanded = false
    }

    fun openPanel() {
        panelOpen = true
    }

    fun closePanel() {
        panelOpen = false
    }

    fun unlock() {
        locked = false
    }

    fun lock() {
        panelOpen = false
        controlCenterOpen = false
        expanded = false
        locked = true
    }

    fun openControlCenter() {
        if (locked) return
        panelOpen = false
        controlCenterOpen = true
    }

    fun closeControlCenter() {
        controlCenterOpen = false
    }

    fun toggleQuick(key: String) {
        enabledToggles = if (key in enabledToggles) {
            enabledToggles - key
        } else {
            enabledToggles + key
        }
    }

    fun setBrightness(value: Float) {
        brightness = value
    }

    fun setVolume(value: Float) {
        volume = value
    }

    fun selectAccent(option: Accent) {
        accent = option
    }

    fun togglePlay() {
        val music = content as? IslandContent.Music ?: return
        content = music.copy(playing = !music.playing)
    }

    fun nextTrack() {
        trackIndex = (trackIndex + 1) % playlist.size
        switchTrack()
    }

    fun previousTrack() {
        trackIndex = (trackIndex - 1 + playlist.size) % playlist.size
        switchTrack()
    }

    fun toggleTimer() {
        val timer = content as? IslandContent.Timer ?: return
        content = timer.copy(running = !timer.running)
    }

    fun resetTimer() {
        val timer = content as? IslandContent.Timer ?: return
        content = timer.copy(remaining = timer.total, running = true)
    }

    fun acceptCall() {
        val call = content as? IslandContent.Call ?: return
        content = call.copy(connected = true, seconds = 0)
        expanded = true
        startCallTicker()
    }

    fun declineCall() {
        dismiss()
    }

    private fun switchTrack() {
        val track = playlist[trackIndex]
        content = IslandContent.Music(
            title = track.title,
            artist = track.artist,
            playing = true,
            progress = 0f,
        )
        startMusicTicker()
    }

    private fun startMusicTicker() {
        ticker?.cancel()
        ticker = viewModelScope.launch {
            while (isActive) {
                delay(500)
                val music = content as? IslandContent.Music ?: break
                if (!music.playing) continue
                val next = music.progress + 0.008f
                content = music.copy(progress = if (next >= 1f) 0f else next)
            }
        }
    }

    private fun startTimerTicker() {
        ticker?.cancel()
        ticker = viewModelScope.launch {
            while (isActive) {
                delay(1_000)
                val timer = content as? IslandContent.Timer ?: break
                if (!timer.running) continue
                val remaining = (timer.remaining - 1).coerceAtLeast(0)
                content = timer.copy(remaining = remaining, running = remaining > 0)
            }
        }
    }

    private fun startCallTicker() {
        ticker?.cancel()
        ticker = viewModelScope.launch {
            while (isActive) {
                delay(1_000)
                val call = content as? IslandContent.Call ?: break
                if (!call.connected) continue
                content = call.copy(seconds = call.seconds + 1)
            }
        }
    }

    private fun startChargingTicker() {
        ticker?.cancel()
        ticker = viewModelScope.launch {
            while (isActive) {
                delay(900)
                val charging = content as? IslandContent.Charging ?: break
                if (charging.percent >= 100) break
                content = charging.copy(percent = (charging.percent + 1).coerceAtMost(100))
            }
        }
    }
}
