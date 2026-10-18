package com.claudio.memoria.ui.game

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.claudio.memoria.domain.engine.PairsCard
import com.claudio.memoria.ui.theme.CardBack
import com.claudio.memoria.ui.viewmodel.PairsUiState

@Composable
fun PairsGame(
    level: Int,
    state: PairsUiState,
    onFlip: (Int) -> Unit,
    onRepeat: () -> Unit,
    onExit: () -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        PlayScaffold(
            title = "Encontrar parejas",
            level = level,
            onBack = onExit,
            subtitle = "Toca dos cartas iguales"
        ) {
            Row(Modifier.fillMaxWidth()) {
                Text(
                    "Intentos: ${state.attempts}",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.weight(1f))
                Text(
                    "Parejas: ${state.matchedPairs.size} de ${state.pairCount}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(12.dp))
            val columns = when {
                state.cards.size <= 6 -> 2
                state.cards.size <= 12 -> 3
                else -> 4
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(2.dp)
            ) {
                items(state.cards.size) { index ->
                    val card = state.cards[index]
                    FlipCard(
                        card = card,
                        visible = state.isVisible(index),
                        onClick = { onFlip(index) }
                    )
                }
            }
        }

        FinishedOverlay(
            visible = state.finished,
            correct = state.pairCount,
            total = state.attempts,
            onRepeat = onRepeat,
            onExit = onExit
        )
    }
}

@Composable
private fun FlipCard(card: PairsCard, visible: Boolean, onClick: () -> Unit) {
    val rotation by animateFloatAsState(
        targetValue = if (visible) 180f else 0f,
        animationSpec = tween(durationMillis = 280),
        label = "cardFlip"
    )
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (rotation > 90f) MaterialTheme.colorScheme.surface else CardBack,
        tonalElevation = if (rotation > 90f) 3.dp else 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 14f * density
            }
            .clickable(onClick = onClick)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (rotation <= 90f) {
                Text(
                    text = "?",
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.White
                )
            } else {
                Box(Modifier.graphicsLayer { rotationY = 180f }) {
                    Text(text = card.symbol, fontSize = 42.sp)
                }
            }
        }
    }
}
