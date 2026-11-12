package com.example.originos.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.originos.IslandViewModel
import com.example.originos.home.HomeScreen
import com.example.originos.island.OriginIsland

@Composable
fun DemoRoot(viewModel: IslandViewModel) {
    val accent = viewModel.accent.color

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF050507)),
    ) {
        HomeScreen(accent = accent)

        OriginIsland(
            content = viewModel.content,
            expanded = viewModel.expanded,
            accent = accent,
            onToggle = viewModel::toggleExpanded,
            onCollapse = viewModel::collapse,
            onTogglePlay = viewModel::togglePlay,
            onNext = viewModel::nextTrack,
            onPrevious = viewModel::previousTrack,
            onToggleTimer = viewModel::toggleTimer,
            onResetTimer = viewModel::resetTimer,
            onAccept = viewModel::acceptCall,
            onDecline = viewModel::declineCall,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 8.dp),
        )

        LabButton(
            onClick = viewModel::openPanel,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 20.dp, bottom = 116.dp),
        )
    }

    if (viewModel.panelOpen) {
        IslandLabSheet(viewModel)
    }
}
