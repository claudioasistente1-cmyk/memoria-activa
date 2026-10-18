package com.claudio.memoria.ui.game

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.claudio.memoria.ui.viewmodel.GamePhase
import com.claudio.memoria.ui.viewmodel.SequenceUiState

@Composable
fun SequenceGame(
    level: Int,
    state: SequenceUiState,
    colors: List<String>,
    onColorTap: (Int) -> Unit,
    onRepeat: () -> Unit,
    onExit: () -> Unit
) {
    val palette = remember(colors) {
        colors.map { hex ->
            runCatching { Color(android.graphics.Color.parseColor(hex)) }
                .getOrDefault(Color(0xFF4C8DD9))
        }
    }

    Box(Modifier.fillMaxSize()) {
        PlayScaffold(
            title = "Recordar secuencias",
            level = level,
            onBack = onExit,
            subtitle = when (state.phase) {
                GamePhase.SHOWING -> "Mira con atencion la secuencia"
                GamePhase.INPUT -> "Repeti la secuencia tocando los colores"
                else -> "Secuencia terminada"
            }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(state.length) { index ->
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .then(
                                Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (index < state.tapIndex) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outline
                            },
                            modifier = Modifier.size(12.dp)
                        ) {}
                    }
                    if (index != state.length - 1) Spacer(Modifier.size(8.dp))
                }
            }
            Spacer(Modifier.height(18.dp))

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterVertically)
            ) {
                (0 until state.paletteSize).chunked(2).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        row.forEach { colorIndex ->
                            ColorButton(
                                color = palette[colorIndex % palette.size],
                                highlighted = state.highlighted == colorIndex,
                                enabled = state.phase == GamePhase.INPUT,
                                onClick = { onColorTap(colorIndex) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        repeat(2 - row.size) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        FinishedOverlay(
            visible = state.phase == GamePhase.FINISHED,
            correct = state.correctCount,
            total = state.length,
            onRepeat = onRepeat,
            onExit = onExit
        )
    }
}

@Composable
private fun ColorButton(
    color: Color,
    highlighted: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (highlighted) 1.05f else 1f,
        animationSpec = tween(durationMillis = 160),
        label = "colorScale"
    )
    val shape = RoundedCornerShape(26.dp)
    Surface(
        shape = shape,
        color = color.copy(alpha = if (enabled || highlighted) 1f else 0.55f),
        modifier = modifier
            .aspectRatio(1.15f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .then(
                if (highlighted) Modifier.border(8.dp, Color.White, shape) else Modifier
            )
            .clickable(enabled = enabled, onClick = onClick)
    ) {}
}
