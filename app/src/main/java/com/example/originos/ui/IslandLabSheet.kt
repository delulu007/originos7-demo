package com.example.originos.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.originos.IslandViewModel
import com.example.originos.island.Accent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IslandLabSheet(viewModel: IslandViewModel) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = viewModel::closePanel,
        sheetState = sheetState,
        containerColor = Color(0xFF0E0F14),
        contentColor = Color.White,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.28f))
        },
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp)
                .padding(bottom = 34.dp),
        ) {
            Text(
                text = "Island Lab",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Trigger a live activity and watch the Origin Island expand.",
                color = Color.White.copy(alpha = 0.55f),
                fontSize = 13.sp,
            )
            Spacer(Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                LabAction(
                    emoji = "🎵",
                    title = "Music",
                    subtitle = "Now playing",
                    modifier = Modifier.weight(1f),
                ) {
                    viewModel.playMusic()
                }
                LabAction(
                    emoji = "⏱️",
                    title = "Timer",
                    subtitle = "5 minute countdown",
                    modifier = Modifier.weight(1f),
                ) {
                    viewModel.startTimer()
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                LabAction(
                    emoji = "📞",
                    title = "Call",
                    subtitle = "Incoming call",
                    modifier = Modifier.weight(1f),
                ) {
                    viewModel.incomingCall()
                }
                LabAction(
                    emoji = "🔋",
                    title = "Charging",
                    subtitle = "44W FlashCharge",
                    modifier = Modifier.weight(1f),
                ) {
                    viewModel.charging()
                }
            }
            Spacer(Modifier.height(12.dp))
            LabAction(
                emoji = "✕",
                title = "Dismiss island",
                subtitle = "Return to the idle pill",
                modifier = Modifier.fillMaxWidth(),
            ) {
                viewModel.dismiss()
            }
            Spacer(Modifier.height(12.dp))
            LabAction(
                emoji = "🔒",
                title = "Lock screen",
                subtitle = "Back to the OriginOS lock screen",
                modifier = Modifier.fillMaxWidth(),
            ) {
                viewModel.lock()
            }

            Spacer(Modifier.height(24.dp))
            Text(
                text = "ACCENT",
                color = Color.White.copy(alpha = 0.45f),
                fontSize = 10.sp,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Accent.entries.forEach { option ->
                    val selected = option == viewModel.accent
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(option.color)
                            .border(
                                width = 2.dp,
                                color = Color.White.copy(alpha = if (selected) 0.9f else 0f),
                                shape = CircleShape,
                            )
                            .clickable { viewModel.selectAccent(option) },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (selected) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = Color(0xFF0A0A0F),
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LabAction(
    emoji: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.07f))
            .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Text(emoji, fontSize = 20.sp)
        Spacer(Modifier.height(10.dp))
        Text(
            text = title,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = subtitle,
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun LabButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.12f))
            .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Rounded.Tune,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(7.dp))
        Text(
            text = "Island Lab",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}
