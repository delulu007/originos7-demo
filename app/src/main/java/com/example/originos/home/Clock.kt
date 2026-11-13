package com.example.originos.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay
import java.time.LocalDateTime

/** A once-per-second clock that survives recomposition. */
@Composable
fun rememberClock(): State<LocalDateTime> {
    val state = remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            state.value = LocalDateTime.now()
            delay(1_000L)
        }
    }
    return state
}
