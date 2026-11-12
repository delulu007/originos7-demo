package com.example.originos.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private data class AppItem(val emoji: String, val name: String, val color: Color)

private val apps = listOf(
    AppItem("📷", "Camera", Color(0xFF2F3550)),
    AppItem("🖼️", "Gallery", Color(0xFF4A2F50)),
    AppItem("🎵", "Music", Color(0xFF203A48)),
    AppItem("🌤️", "Weather", Color(0xFF2B3F63)),
    AppItem("⚙️", "Settings", Color(0xFF35353F)),
    AppItem("🕐", "Clock", Color(0xFF1F3A3A)),
    AppItem("📝", "Notes", Color(0xFF4A4022)),
    AppItem("🛍️", "Store", Color(0xFF3B2B4A)),
    AppItem("📁", "Files", Color(0xFF2F3F2F)),
    AppItem("🌐", "Browser", Color(0xFF243B55)),
    AppItem("🧮", "Calculator", Color(0xFF40302A)),
    AppItem("🎨", "Themes", Color(0xFF3A2A45)),
)

private val dockApps = listOf("📞", "💬", "🌐", "📷")

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM d")

/**
 * A mock OriginOS home screen, only there to give the island a believable stage.
 */
@Composable
fun HomeScreen(accent: Color, modifier: Modifier = Modifier) {
    val now by rememberClock()
    val dateText = remember(now.toLocalDate()) { now.toLocalDate().format(dateFormatter) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        accent.copy(alpha = 0.42f),
                        Color(0xFF0B0C16),
                        Color(0xFF040405),
                    ),
                ),
            ),
    ) {
        Box(
            Modifier
                .size(340.dp)
                .offset(x = (-90).dp, y = (-60).dp)
                .background(
                    Brush.radialGradient(
                        listOf(accent.copy(alpha = 0.30f), Color.Transparent),
                    ),
                    CircleShape,
                ),
        )
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .size(280.dp)
                .offset(x = (-80).dp, y = 90.dp)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0x297C5CFF), Color.Transparent),
                    ),
                    CircleShape,
                ),
        )

        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 22.dp),
        ) {
            Spacer(Modifier.height(76.dp))

            Text(
                text = now.toLocalTime().format(timeFormatter),
                color = Color.White,
                fontSize = 56.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = (-2).sp,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = dateText,
                    color = Color.White.copy(alpha = 0.66f),
                    fontSize = 13.sp,
                )
                Text(
                    text = "  ·  ",
                    color = Color.White.copy(alpha = 0.35f),
                    fontSize = 13.sp,
                )
                Text(
                    text = "24° Clear",
                    color = Color.White.copy(alpha = 0.66f),
                    fontSize = 13.sp,
                )
            }

            Spacer(Modifier.height(20.dp))
            WidgetRow()
            Spacer(Modifier.height(24.dp))
            AppGrid()
            Spacer(Modifier.weight(1f))
            Dock()
            Spacer(Modifier.height(12.dp))
            PageDots(accent = accent)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun WidgetRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        GlassCard(Modifier.weight(1f).height(88.dp)) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Text("Weather", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                    Text("☀️", fontSize = 16.sp)
                }
                Column {
                    Text("24°", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Medium)
                    Text(
                        "Clear · H 29° L 18°",
                        color = Color.White.copy(alpha = 0.55f),
                        fontSize = 10.sp,
                    )
                }
            }
        }

        GlassCard(Modifier.weight(1f).height(88.dp)) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Text("Steps", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                    Text("🏃", fontSize = 16.sp)
                }
                Column {
                    Text("6,842", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Medium)
                    Text(
                        "of 10,000 today",
                        color = Color.White.copy(alpha = 0.55f),
                        fontSize = 10.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun AppGrid() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        apps.chunked(4).forEach { rowApps ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                rowApps.forEach { app -> AppIcon(app) }
                repeat(4 - rowApps.size) {
                    Spacer(Modifier.width(64.dp))
                }
            }
        }
    }
}

@Composable
private fun AppIcon(app: AppItem) {
    Column(
        modifier = Modifier.width(64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(17.dp))
                .background(
                    Brush.linearGradient(
                        listOf(app.color, app.color.copy(alpha = 0.72f)),
                    ),
                )
                .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(17.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(app.emoji, fontSize = 24.sp)
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = app.name,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun Dock() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White.copy(alpha = 0.10f))
            .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(28.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            dockApps.forEach { emoji ->
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color.White.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(emoji, fontSize = 22.sp)
                }
            }
        }
    }
}

@Composable
private fun PageDots(accent: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(width = 16.dp, height = 5.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(accent),
        )
        Spacer(Modifier.width(6.dp))
        repeat(2) { index ->
            Box(
                Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.35f)),
            )
            if (index == 0) {
                Spacer(Modifier.width(6.dp))
            }
        }
    }
}

@Composable
private fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(24.dp)),
        content = content,
    )
}

@Composable
private fun rememberClock(): State<LocalDateTime> {
    val state = remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            state.value = LocalDateTime.now()
            delay(1_000L)
        }
    }
    return state
}
