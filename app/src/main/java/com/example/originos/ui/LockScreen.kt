package com.example.originos.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.originos.home.rememberClock
import java.time.format.DateTimeFormatter

private val lockTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val lockDateFormatter = DateTimeFormatter.ofPattern("EEEE, MMM d")

/**
 * A mock OriginOS lock screen. Swipe up (or tap the hint) to unlock.
 * Nothing here is a real keyguard — it is only a visual stage for the demo.
 */
@Composable
fun LockScreen(
    accent: Color,
    onUnlock: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val now by rememberClock()
    var dragY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        accent.copy(alpha = 0.38f),
                        Color(0xFF0A0B12),
                        Color(0xFF030304),
                    ),
                ),
            )
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        if (dragY < -150f) onUnlock() else dragY = 0f
                    },
                    onDragCancel = { dragY = 0f },
                    onVerticalDrag = { change, amount ->
                        change.consume()
                        dragY = (dragY + amount).coerceAtMost(0f)
                    },
                )
            }
            .graphicsLayer {
                translationY = dragY
                alpha = 1f - ((-dragY) / 1600f).coerceIn(0f, 0.55f)
            },
    ) {
        Box(
            Modifier
                .size(300.dp)
                .align(Alignment.TopEnd)
                .background(
                    Brush.radialGradient(
                        listOf(accent.copy(alpha = 0.26f), Color.Transparent),
                    ),
                    CircleShape,
                ),
        )
        Box(
            Modifier
                .size(260.dp)
                .align(Alignment.BottomStart)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0x297C5CFF), Color.Transparent),
                    ),
                    CircleShape,
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(34.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(12.dp),
                )
                Spacer(Modifier.width(5.dp))
                Text(
                    text = "OriginOS",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    letterSpacing = 2.sp,
                )
            }

            Spacer(Modifier.height(12.dp))
            Text(
                text = now.format(lockTimeFormatter),
                color = Color.White,
                fontSize = 68.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = (-3).sp,
            )
            Text(
                text = now.format(lockDateFormatter),
                color = Color.White.copy(alpha = 0.66f),
                fontSize = 13.sp,
            )
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("☀️", fontSize = 12.sp)
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "24° Clear · Hangzhou",
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 12.sp,
                )
            }

            Spacer(Modifier.height(30.dp))
            NotificationCard(
                emoji = "💬",
                title = "Maya Sharma",
                body = "Are we still on for dinner at 8?",
                time = "now",
            )
            Spacer(Modifier.height(10.dp))
            NotificationCard(
                emoji = "🎵",
                title = "Now playing",
                body = "Midnight City · M83",
                time = "2m",
            )

            Spacer(Modifier.weight(1f))
            Column(
                modifier = Modifier.clickable(onClick = onUnlock),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowUp,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.55f),
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = "Swipe up to unlock",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                )
            }
            Spacer(Modifier.height(18.dp))
        }
    }
}

@Composable
private fun NotificationCard(
    emoji: String,
    title: String,
    body: String,
    time: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White.copy(alpha = 0.09f))
            .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(22.dp))
            .padding(13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(emoji, fontSize = 18.sp)
        }
        Spacer(Modifier.width(11.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = body,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = time,
            color = Color.White.copy(alpha = 0.4f),
            fontSize = 10.sp,
        )
    }
}
