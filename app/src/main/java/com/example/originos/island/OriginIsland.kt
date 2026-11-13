package com.example.originos.island

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.CallEnd
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

/**
 * The Origin Island: a black capsule that morphs into a live-activity card.
 *
 * It is drawn entirely in-app, so it works on any Android 8+ device without
 * notification access, overlay permission or a vivo ROM.
 */
@Composable
fun OriginIsland(
    content: IslandContent,
    expanded: Boolean,
    accent: Color,
    onToggle: () -> Unit,
    onCollapse: () -> Unit,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onReply: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val targetWidth = when {
        expanded -> 336.dp
        content is IslandContent.Call -> 216.dp
        content is IslandContent.Message -> 206.dp
        content is IslandContent.Music -> 158.dp
        content is IslandContent.Timer -> 126.dp
        content is IslandContent.Charging -> 118.dp
        else -> 92.dp
    }
    val targetHeight = when {
        !expanded -> when {
            content is IslandContent.Call -> 46.dp
            content is IslandContent.Message -> 40.dp
            else -> 36.dp
        }
        content is IslandContent.Music -> 174.dp
        content is IslandContent.Timer -> 214.dp
        content is IslandContent.Call -> 230.dp
        content is IslandContent.Charging -> 150.dp
        content is IslandContent.Message -> 172.dp
        else -> 120.dp
    }

    val width by animateDpAsState(
        targetValue = targetWidth,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMediumLow),
        label = "islandWidth",
    )
    val height by animateDpAsState(
        targetValue = targetHeight,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMediumLow),
        label = "islandHeight",
    )
    val corner by animateDpAsState(
        targetValue = if (expanded) 30.dp else 24.dp,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
        label = "islandCorner",
    )
    val shape = RoundedCornerShape(corner)
    val interaction = remember { MutableInteractionSource() }
    val scope = rememberCoroutineScope()
    val dragOffset = remember { Animatable(0f) }

    // Swipe the island down to dismiss the current activity.
    val dismissGesture = if (content is IslandContent.Idle) {
        Modifier
    } else {
        Modifier.pointerInput(Unit) {
            val threshold = 56.dp.toPx()
            detectVerticalDragGestures(
                onDragEnd = {
                    if (dragOffset.value > threshold) {
                        scope.launch {
                            dragOffset.animateTo(
                                targetValue = size.height * 1.5f,
                                animationSpec = tween(durationMillis = 180),
                            )
                            onDismiss()
                            dragOffset.snapTo(0f)
                        }
                    } else {
                        scope.launch {
                            dragOffset.animateTo(
                                targetValue = 0f,
                                animationSpec = spring(dampingRatio = 0.8f),
                            )
                        }
                    }
                },
                onDragCancel = {
                    scope.launch {
                        dragOffset.animateTo(0f, spring(dampingRatio = 0.8f))
                    }
                },
                onVerticalDrag = { change, amount ->
                    change.consume()
                    scope.launch {
                        dragOffset.snapTo((dragOffset.value + amount).coerceAtLeast(0f))
                    }
                },
            )
        }
    }

    Box(modifier.then(dismissGesture), contentAlignment = Alignment.TopCenter) {
        // Ambient glow that hints at the accent colour.
        Box(
            Modifier
                .width(width + 60.dp)
                .height(height + 48.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(accent.copy(alpha = 0.26f), Color.Transparent),
                    ),
                    shape = RoundedCornerShape(48.dp),
                ),
        )

        Box(
            modifier = Modifier
                .width(width)
                .height(height)
                .graphicsLayer { translationY = dragOffset.value }
                .shadow(elevation = 24.dp, shape = shape)
                .clip(shape)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF191A20), Color(0xFF050507)),
                    ),
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.18f), Color.White.copy(alpha = 0.04f)),
                    ),
                    shape = shape,
                )
                .then(
                    if (expanded) {
                        Modifier
                    } else {
                        Modifier.clickable(
                            interactionSource = interaction,
                            indication = null,
                            onClick = onToggle,
                        )
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            AnimatedContent(
                targetState = expanded,
                transitionSpec = {
                    (fadeIn(tween(240, delayMillis = 90)) + scaleIn(initialScale = 0.94f)) togetherWith
                        (fadeOut(tween(120)) + scaleOut(targetScale = 0.98f))
                },
                label = "islandExpansion",
            ) { isExpanded ->
                if (isExpanded) {
                    ExpandedIslandContent(
                        content = content,
                        accent = accent,
                        onCollapse = onCollapse,
                        onTogglePlay = onTogglePlay,
                        onNext = onNext,
                        onPrevious = onPrevious,
                        onToggleTimer = onToggleTimer,
                        onResetTimer = onResetTimer,
                        onAccept = onAccept,
                        onDecline = onDecline,
                        onReply = onReply,
                    )
                } else {
                    CompactIslandContent(
                        content = content,
                        accent = accent,
                        onAccept = onAccept,
                        onDecline = onDecline,
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactIslandContent(
    content: IslandContent,
    accent: Color,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
) {
    when (content) {
        IslandContent.Idle -> CameraLens()

        is IslandContent.Music -> Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AlbumArt(size = 24.dp, accent = accent)
            Spacer(Modifier.width(8.dp))
            Text(
                text = content.title,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(8.dp))
            Equalizer(playing = content.playing, accent = accent)
        }

        is IslandContent.Timer -> Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Rounded.Timer,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(7.dp))
            Text(
                text = formatDuration(content.remaining),
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        is IslandContent.Charging -> Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Rounded.BatteryChargingFull,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "${content.percent}%",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        is IslandContent.Call -> Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Avatar(name = content.name, size = 26.dp, accent = accent)
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = content.name,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = if (content.connected) formatDuration(content.seconds) else "Incoming call",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 9.sp,
                )
            }
            CircleAction(
                size = 28.dp,
                background = Color(0xFFE5484D),
                icon = Icons.Rounded.CallEnd,
                onClick = onDecline,
            )
            Spacer(Modifier.width(6.dp))
            CircleAction(
                size = 28.dp,
                background = Color(0xFF30C46B),
                icon = Icons.Rounded.Call,
                onClick = onAccept,
            )
        }

        is IslandContent.Message -> Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Avatar(name = content.sender, size = 24.dp, accent = accent)
            Spacer(Modifier.width(7.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = content.sender,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = content.text,
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.width(6.dp))
            Box(
                Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(accent),
            )
        }
    }
}

@Composable
private fun ExpandedIslandContent(
    content: IslandContent,
    accent: Color,
    onCollapse: () -> Unit,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onReply: () -> Unit,
) {
    when (content) {
        IslandContent.Idle -> Unit

        is IslandContent.Music -> ExpandedMusic(
            music = content,
            accent = accent,
            onCollapse = onCollapse,
            onTogglePlay = onTogglePlay,
            onNext = onNext,
            onPrevious = onPrevious,
        )

        is IslandContent.Timer -> ExpandedTimer(
            timer = content,
            accent = accent,
            onCollapse = onCollapse,
            onToggleTimer = onToggleTimer,
            onResetTimer = onResetTimer,
        )

        is IslandContent.Call -> ExpandedCall(
            call = content,
            accent = accent,
            onCollapse = onCollapse,
            onAccept = onAccept,
            onDecline = onDecline,
        )

        is IslandContent.Charging -> ExpandedCharging(
            charging = content,
            accent = accent,
            onCollapse = onCollapse,
        )

        is IslandContent.Message -> ExpandedMessage(
            message = content,
            accent = accent,
            onCollapse = onCollapse,
            onReply = onReply,
        )
    }
}

@Composable
private fun ExpandedMusic(
    music: IslandContent.Music,
    accent: Color,
    onCollapse: () -> Unit,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp, vertical = 16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AlbumArt(size = 52.dp, accent = accent)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = music.title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = music.artist,
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            CircleAction(
                size = 28.dp,
                background = Color.White.copy(alpha = 0.08f),
                icon = Icons.Rounded.Close,
                iconTint = Color.White.copy(alpha = 0.8f),
                onClick = onCollapse,
            )
        }

        Spacer(Modifier.height(14.dp))
        ProgressLine(progress = music.progress, accent = accent)
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = formatDuration((music.progress * 218).toInt()),
                color = Color.White.copy(alpha = 0.45f),
                fontSize = 10.sp,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "3:38",
                color = Color.White.copy(alpha = 0.45f),
                fontSize = 10.sp,
            )
        }

        Spacer(Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircleAction(
                size = 34.dp,
                background = Color.Transparent,
                icon = Icons.Rounded.SkipPrevious,
                onClick = onPrevious,
            )
            Spacer(Modifier.width(18.dp))
            CircleAction(
                size = 42.dp,
                background = accent,
                icon = if (music.playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                iconTint = Color(0xFF0A0A0F),
                onClick = onTogglePlay,
            )
            Spacer(Modifier.width(18.dp))
            CircleAction(
                size = 34.dp,
                background = Color.Transparent,
                icon = Icons.Rounded.SkipNext,
                onClick = onNext,
            )
        }
    }
}

@Composable
private fun ExpandedTimer(
    timer: IslandContent.Timer,
    accent: Color,
    onCollapse: () -> Unit,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "TIMER",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 10.sp,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.weight(1f))
            CircleAction(
                size = 28.dp,
                background = Color.White.copy(alpha = 0.08f),
                icon = Icons.Rounded.Close,
                iconTint = Color.White.copy(alpha = 0.8f),
                onClick = onCollapse,
            )
        }

        Spacer(Modifier.height(8.dp))
        Box(Modifier.size(100.dp), contentAlignment = Alignment.Center) {
            val progress = if (timer.total == 0) 0f else timer.remaining.toFloat() / timer.total
            Canvas(Modifier.fillMaxSize()) {
                val stroke = 6.dp.toPx()
                drawArc(
                    color = Color.White.copy(alpha = 0.12f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
                drawArc(
                    color = accent,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = formatDuration(timer.remaining),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = if (timer.running) "Running" else "Paused",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 10.sp,
                )
            }
        }

        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PillButton(
                label = if (timer.running) "Pause" else "Resume",
                accent = accent,
                onClick = onToggleTimer,
            )
            PillButton(
                label = "Reset",
                accent = accent,
                filled = false,
                onClick = onResetTimer,
            )
        }
    }
}

@Composable
private fun ExpandedCall(
    call: IslandContent.Call,
    accent: Color,
    onCollapse: () -> Unit,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (call.connected) "ON CALL" else "INCOMING CALL",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 10.sp,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.weight(1f))
            CircleAction(
                size = 28.dp,
                background = Color.White.copy(alpha = 0.08f),
                icon = Icons.Rounded.Close,
                iconTint = Color.White.copy(alpha = 0.8f),
                onClick = onCollapse,
            )
        }

        Spacer(Modifier.height(6.dp))
        Avatar(name = call.name, size = 64.dp, accent = accent)
        Spacer(Modifier.height(10.dp))
        Text(
            text = call.name,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = if (call.connected) formatDuration(call.seconds) else "OriginOS · Mobile",
            color = Color.White.copy(alpha = 0.55f),
            fontSize = 11.sp,
        )

        Spacer(Modifier.weight(1f))
        if (call.connected) {
            Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                CircleAction(
                    size = 44.dp,
                    background = Color.White.copy(alpha = 0.10f),
                    icon = Icons.Rounded.MicOff,
                    onClick = {},
                )
                CircleAction(
                    size = 44.dp,
                    background = Color.White.copy(alpha = 0.10f),
                    icon = Icons.Rounded.VolumeUp,
                    onClick = {},
                )
                CircleAction(
                    size = 44.dp,
                    background = Color(0xFFE5484D),
                    icon = Icons.Rounded.CallEnd,
                    onClick = onDecline,
                )
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(40.dp)) {
                CircleAction(
                    size = 52.dp,
                    background = Color(0xFFE5484D),
                    icon = Icons.Rounded.CallEnd,
                    onClick = onDecline,
                )
                CircleAction(
                    size = 52.dp,
                    background = Color(0xFF30C46B),
                    icon = Icons.Rounded.Call,
                    onClick = onAccept,
                )
            }
        }
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun ExpandedCharging(
    charging: IslandContent.Charging,
    accent: Color,
    onCollapse: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp, vertical = 16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accent.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.BatteryChargingFull,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(24.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Super fast charging",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "44W · vivo FlashCharge",
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 11.sp,
                )
            }
            CircleAction(
                size = 28.dp,
                background = Color.White.copy(alpha = 0.08f),
                icon = Icons.Rounded.Close,
                iconTint = Color.White.copy(alpha = 0.8f),
                onClick = onCollapse,
            )
        }

        Spacer(Modifier.height(18.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "${charging.percent}",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Light,
            )
            Text(
                text = "%",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 4.dp),
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = if (charging.percent >= 100) {
                    "Fully charged"
                } else {
                    "About ${((100 - charging.percent) * 0.4f).toInt()} min left"
                },
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp,
                modifier = Modifier.padding(bottom = 6.dp),
            )
        }
        Spacer(Modifier.height(10.dp))
        ProgressLine(progress = charging.percent / 100f, accent = accent)
    }
}

@Composable
private fun ExpandedMessage(
    message: IslandContent.Message,
    accent: Color,
    onCollapse: () -> Unit,
    onReply: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Avatar(name = message.sender, size = 34.dp, accent = accent)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = message.sender,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "Message · now",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 10.sp,
                )
            }
            CircleAction(
                size = 26.dp,
                background = Color.White.copy(alpha = 0.08f),
                icon = Icons.Rounded.Close,
                iconTint = Color.White.copy(alpha = 0.8f),
                onClick = onCollapse,
            )
        }

        Spacer(Modifier.height(10.dp))
        Text(
            text = message.text,
            color = Color.White.copy(alpha = 0.88f),
            fontSize = 12.sp,
            lineHeight = 16.sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )

        Spacer(Modifier.weight(1f))
        if (message.replied) {
            Text(
                text = "Reply sent",
                color = accent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
            )
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PillButton(
                    label = "Reply",
                    accent = accent,
                    onClick = onReply,
                )
                PillButton(
                    label = "Later",
                    accent = accent,
                    filled = false,
                    onClick = onCollapse,
                )
            }
        }
    }
}

@Composable
private fun ProgressLine(
    progress: Float,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(Color.White.copy(alpha = 0.14f)),
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .clip(RoundedCornerShape(2.dp))
                .background(accent),
        )
    }
}

@Composable
private fun PillButton(
    label: String,
    accent: Color,
    onClick: () -> Unit,
    filled: Boolean = true,
) {
    val background = if (filled) accent else Color.White.copy(alpha = 0.10f)
    val foreground = if (filled) Color(0xFF0A0A0F) else Color.White
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(background)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 8.dp),
    ) {
        Text(
            text = label,
            color = foreground,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun CircleAction(
    size: Dp,
    background: Color,
    icon: ImageVector,
    onClick: () -> Unit,
    iconTint: Color = Color.White,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(background)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(size * 0.5f),
        )
    }
}

@Composable
private fun AlbumArt(size: Dp, accent: Color) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.32f))
            .background(Brush.linearGradient(listOf(accent, Color(0xFF8B5CF6)))),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "♫",
            color = Color.White,
            fontSize = (size.value * 0.44f).sp,
        )
    }
}

@Composable
private fun Avatar(name: String, size: Dp, accent: Color) {
    val initials = remember(name) {
        name.trim()
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
    }
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(accent.copy(alpha = 0.9f), Color(0xFF8B5CF6)),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontSize = (size.value * 0.34f).sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun Equalizer(playing: Boolean, accent: Color) {
    val transition = rememberInfiniteTransition(label = "equalizer")
    val bar1 by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(420, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bar1",
    )
    val bar2 by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(560, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bar2",
    )
    val bar3 by transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(340, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bar3",
    )
    val heights = listOf(
        if (playing) bar1 else 0.45f,
        if (playing) bar2 else 0.75f,
        if (playing) bar3 else 0.55f,
    )

    Canvas(Modifier.size(width = 18.dp, height = 14.dp)) {
        val barWidth = size.width / 5f
        val step = size.width / 3f
        heights.forEachIndexed { index, value ->
            val barHeight = (size.height * value).coerceAtLeast(barWidth)
            drawRoundRect(
                color = accent,
                topLeft = Offset(index * step, size.height - barHeight),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f),
            )
        }
    }
}

@Composable
private fun CameraLens() {
    Box(
        modifier = Modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(Color(0xFF1E1F26))
            .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFF3D4C7A), Color(0xFF0B0C10)),
                    ),
                ),
        )
    }
}
