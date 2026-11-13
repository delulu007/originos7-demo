package com.example.originos.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AirplanemodeActive
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.BatterySaver
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.Brightness6
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DoNotDisturbOn
import androidx.compose.material.icons.rounded.FlashlightOn
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.ScreenRotation
import androidx.compose.material.icons.rounded.SignalCellularAlt
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.originos.IslandViewModel
import com.example.originos.island.IslandContent

private data class QuickToggle(
    val key: String,
    val label: String,
    val icon: ImageVector,
)

private val quickToggles = listOf(
    QuickToggle("wifi", "Wi-Fi", Icons.Rounded.Wifi),
    QuickToggle("bluetooth", "Bluetooth", Icons.Rounded.Bluetooth),
    QuickToggle("mobile", "Mobile data", Icons.Rounded.SignalCellularAlt),
    QuickToggle("flashlight", "Flashlight", Icons.Rounded.FlashlightOn),
    QuickToggle("airplane", "Airplane", Icons.Rounded.AirplanemodeActive),
    QuickToggle("dnd", "Do not disturb", Icons.Rounded.DoNotDisturbOn),
    QuickToggle("rotate", "Auto-rotate", Icons.Rounded.ScreenRotation),
    QuickToggle("saver", "Battery saver", Icons.Rounded.BatterySaver),
)

/**
 * OriginOS-style control center: swipe down from the top (or tap the chevron)
 * to pull it over the home screen.
 */
@Composable
fun ControlCenter(
    viewModel: IslandViewModel,
    accent: Color,
    onClose: () -> Unit,
) {
    val scrimInteraction = remember { MutableInteractionSource() }

    AnimatedVisibility(
        visible = viewModel.controlCenterOpen,
        enter = fadeIn(tween(200)),
        exit = fadeOut(tween(160)),
    ) {
        var dragY by remember { mutableFloatStateOf(0f) }

        Box(
            Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    val threshold = 64.dp.toPx()
                    detectVerticalDragGestures(
                        onDragEnd = {
                            if (dragY < -threshold) onClose() else dragY = 0f
                        },
                        onDragCancel = { dragY = 0f },
                        onVerticalDrag = { change, amount ->
                            change.consume()
                            dragY = (dragY + amount).coerceAtMost(0f)
                        },
                    )
                },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f))
                    .clickable(
                        interactionSource = scrimInteraction,
                        indication = null,
                        onClick = onClose,
                    ),
            )
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(tween(320)) { -it } + fadeIn(tween(220)),
                exit = slideOutVertically(tween(200)) { -it } + fadeOut(tween(160)),
            ) {
                ControlCenterPanel(
                    viewModel = viewModel,
                    accent = accent,
                    onClose = onClose,
                    dragOffset = dragY,
                )
            }
        }
    }
}

@Composable
private fun ControlCenterPanel(
    viewModel: IslandViewModel,
    accent: Color,
    onClose: () -> Unit,
    dragOffset: Float,
) {
    val panelInteraction = remember { MutableInteractionSource() }
    val panelShape = RoundedCornerShape(bottomStart = 34.dp, bottomEnd = 34.dp)

    Column(
        modifier = Modifier
            .graphicsLayer { translationY = dragOffset }
            .fillMaxWidth()
            .clip(panelShape)
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF15161D), Color(0xFF0B0C11)),
                ),
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.14f), Color.Transparent),
                ),
                shape = panelShape,
            )
            .clickable(interactionSource = panelInteraction, indication = null) {}
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 8.dp, bottom = 24.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Control Center",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "OriginOS 7",
                    color = Color.White.copy(alpha = 0.45f),
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                )
            }
            CircleIconButton(
                icon = Icons.Rounded.Close,
                background = Color.White.copy(alpha = 0.08f),
                onClick = onClose,
            )
        }

        Spacer(Modifier.height(18.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            quickToggles.chunked(4).forEach { rowToggles ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    rowToggles.forEach { toggle ->
                        QuickToggleTile(
                            toggle = toggle,
                            enabled = toggle.key in viewModel.enabledToggles,
                            accent = accent,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.toggleQuick(toggle.key) },
                        )
                    }
                    repeat(4 - rowToggles.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        SliderRow(
            icon = Icons.Rounded.Brightness6,
            value = viewModel.brightness,
            accent = accent,
            onChange = viewModel::setBrightness,
        )
        SliderRow(
            icon = Icons.Rounded.VolumeUp,
            value = viewModel.volume,
            accent = accent,
            onChange = viewModel::setVolume,
        )

        Spacer(Modifier.height(6.dp))
        MediaCard(viewModel = viewModel, accent = accent)

        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Rounded.BatteryChargingFull,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "44W FlashCharge · 63%",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "OriginOS 7",
                color = Color.White.copy(alpha = 0.28f),
                fontSize = 10.sp,
            )
        }

        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Box(
                Modifier
                    .size(width = 44.dp, height = 4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.20f)),
            )
        }
    }
}

@Composable
private fun QuickToggleTile(
    toggle: QuickToggle,
    enabled: Boolean,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(if (enabled) accent else Color.White.copy(alpha = 0.08f))
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = if (enabled) 0.25f else 0.08f),
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = toggle.icon,
                contentDescription = toggle.label,
                tint = if (enabled) Color(0xFF0A0A0F) else Color.White.copy(alpha = 0.85f),
                modifier = Modifier.size(22.dp),
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = toggle.label,
            color = Color.White.copy(alpha = 0.65f),
            fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun SliderRow(
    icon: ImageVector,
    value: Float,
    accent: Color,
    onChange: (Float) -> Unit,
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.75f),
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(10.dp))
        Slider(
            value = value,
            onValueChange = onChange,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = accent,
                activeTrackColor = accent,
                inactiveTrackColor = Color.White.copy(alpha = 0.14f),
            ),
        )
    }
}

@Composable
private fun MediaCard(viewModel: IslandViewModel, accent: Color) {
    val music = viewModel.content as? IslandContent.Music

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White.copy(alpha = 0.06f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(22.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.linearGradient(listOf(accent, Color(0xFF8B5CF6)))),
            contentAlignment = Alignment.Center,
        ) {
            Text("♫", color = Color.White, fontSize = 18.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = music?.title ?: "Nothing playing",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = music?.artist ?: "Tap play to start the demo track",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.width(8.dp))
        CircleIconButton(
            icon = if (music?.playing == true) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
            background = accent,
            tint = Color(0xFF0A0A0F),
            onClick = {
                if (music == null) viewModel.playMusic() else viewModel.togglePlay()
            },
        )
    }
}

@Composable
private fun CircleIconButton(
    icon: ImageVector,
    background: Color,
    onClick: () -> Unit,
    tint: Color = Color.White.copy(alpha = 0.85f),
    size: Dp = 32.dp,
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
            tint = tint,
            modifier = Modifier.size(size * 0.5f),
        )
    }
}

/** Small chevron in the top-left corner, for discoverability. */
@Composable
fun ControlCenterHandle(
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(width = 36.dp, height = 22.dp)
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.10f))
            .clickable(onClick = onOpen),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Rounded.KeyboardArrowDown,
            contentDescription = "Open Control Center",
            tint = Color.White.copy(alpha = 0.65f),
            modifier = Modifier.size(16.dp),
        )
    }
}

/** Invisible strip along the status bar that opens the control center on a downward swipe. */
@Composable
fun ControlCenterSwipeArea(
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var pulled by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = { pulled = 0f },
                    onDragCancel = { pulled = 0f },
                    onVerticalDrag = { change, amount ->
                        change.consume()
                        pulled += amount
                        if (pulled > 36f) {
                            pulled = 0f
                            onOpen()
                        }
                    },
                )
            },
    )
}
