package com.claudio.memoria.ui.game

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.claudio.memoria.ui.components.SectionCard
import com.claudio.memoria.ui.viewmodel.LogicUiState

@Composable
fun LogicGame(
    level: Int,
    state: LogicUiState,
    onAnswer: (Int) -> Unit,
    onRepeat: () -> Unit,
    onExit: () -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        PlayScaffold(
            title = "Patrones y logica",
            level = level,
            onBack = onExit,
            subtitle = "Descubri que numero falta"
        ) {
            val series = state.series
            if (series == null) {
                Text("Preparando la actividad...", style = MaterialTheme.typography.bodyLarge)
            } else {
                Text(
                    "Ejercicio ${state.index + 1} de ${state.total}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))
                SectionCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        series.items.forEach { item ->
                            Surface(
                                shape = CircleShape,
                                color = if (item == null) {
                                    MaterialTheme.colorScheme.secondaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                },
                                modifier = Modifier.size(54.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = item?.toString() ?: "?",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = if (item == null) {
                                            MaterialTheme.colorScheme.onSecondaryContainer
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.weight(1f))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    series.options.chunked(2).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            row.forEach { option ->
                                AnswerButton(
                                    text = option.toString(),
                                    correct = state.reveal && option == series.answer,
                                    wrong = state.reveal && state.selected == option && option != series.answer,
                                    enabled = !state.reveal,
                                    onClick = { onAnswer(option) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (row.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
                Spacer(Modifier.weight(1f))
            }
        }

        FinishedOverlay(
            visible = state.finished,
            correct = state.correct,
            total = state.total,
            onRepeat = onRepeat,
            onExit = onExit
        )
    }
}
