package com.claudio.memoria.ui.game

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.claudio.memoria.ui.components.BigButton
import com.claudio.memoria.ui.sound.SoundEffects
import com.claudio.memoria.ui.theme.Danger
import com.claudio.memoria.ui.theme.Success
import com.claudio.memoria.ui.viewmodel.GamePhase
import com.claudio.memoria.ui.viewmodel.VisualMemoryUiState

@Composable
fun VisualMemoryGame(
    level: Int,
    state: VisualMemoryUiState,
    sound: SoundEffects,
    onToggleCell: (Int) -> Unit,
    onSubmit: () -> Unit,
    onRepeat: () -> Unit,
    onExit: () -> Unit
) {
    LaunchedEffect(state.phase) {
        if (state.phase == GamePhase.FINISHED) {
            if (state.correctCount * 2 >= state.targetCount) sound.success() else sound.error()
        }
    }
    val round = state.round
    val gridSize = round.gridSize
    val cellCount = gridSize * gridSize

    Box(Modifier.fillMaxSize()) {
        PlayScaffold(
            title = "Memoria visual",
            level = level,
            onBack = onExit,
            subtitle = when (state.phase) {
                GamePhase.MEMORIZE -> "Memorizá dónde están las figuras"
                GamePhase.RECALL -> "Tocá las casillas donde estaban"
                else -> "Resultado"
            }
        ) {
            if (state.phase == GamePhase.MEMORIZE) {
                var remaining by remember(state.round) { mutableFloatStateOf(1f) }
                LaunchedEffect(state.round, state.phase) {
                    if (state.phase == GamePhase.MEMORIZE) {
                        animate(
                            initialValue = 1f,
                            targetValue = 0f,
                            animationSpec = tween(durationMillis = state.memorizeMillis.toInt())
                        ) { value, _ -> remaining = value }
                    }
                }
                LinearProgressIndicator(
                    progress = { remaining },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp),
                )
                Spacer(Modifier.height(12.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(gridSize) { rowIndex ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(gridSize) { columnIndex ->
                            val cell = rowIndex * gridSize + columnIndex
                            MemoryCell(
                                cell = cell,
                                symbol = round.items[cell],
                                selected = cell in state.selected,
                                reveal = state.phase == GamePhase.FINISHED,
                                memorize = state.phase == GamePhase.MEMORIZE,
                                onClick = { onToggleCell(cell) },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            )
                        }
                    }
                }
            }

            if (state.phase == GamePhase.RECALL) {
                Spacer(Modifier.height(12.dp))
                Text(
                    "Elegiste ${state.selected.size} casillas",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                BigButton(
                    text = "Listo",
                    onClick = onSubmit,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (state.phase == GamePhase.FINISHED) {
                Spacer(Modifier.height(10.dp))
                Text(
                    "Encontradas ${state.correctCount} de ${state.targetCount}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }

        FinishedOverlay(
            visible = state.phase == GamePhase.FINISHED,
            correct = state.correctCount,
            total = state.targetCount,
            onRepeat = onRepeat,
            onExit = onExit
        )
    }
}

@Composable
private fun MemoryCell(
    cell: Int,
    symbol: String?,
    selected: Boolean,
    reveal: Boolean,
    memorize: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val wasTarget = symbol != null
    val backgroundColor = when {
        reveal && wasTarget -> Success.copy(alpha = 0.22f)
        reveal && selected && !wasTarget -> Danger.copy(alpha = 0.22f)
        !reveal && selected -> MaterialTheme.colorScheme.primaryContainer
        memorize && wasTarget -> MaterialTheme.colorScheme.surface
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val showSymbol = (memorize && wasTarget) || (reveal && wasTarget)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        tonalElevation = 1.dp,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (showSymbol && symbol != null) {
                Text(text = symbol, fontSize = fontSizeForCell(cell))
            } else if (reveal && selected && !wasTarget) {
                Text(
                    "X",
                    style = MaterialTheme.typography.titleLarge,
                    color = Danger
                )
            }
        }
    }
}

private fun fontSizeForCell(cell: Int): androidx.compose.ui.unit.TextUnit = 34.sp
