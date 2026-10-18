package com.claudio.memoria.ui.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.claudio.memoria.ui.components.SectionCard
import com.claudio.memoria.ui.viewmodel.MathUiState

@Composable
fun MathGame(
    level: Int,
    state: MathUiState,
    onAnswer: (Int) -> Unit,
    onRepeat: () -> Unit,
    onExit: () -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        PlayScaffold(
            title = "Calculo mental",
            level = level,
            onBack = onExit,
            subtitle = "Elegi la respuesta correcta"
        ) {
            val question = state.question
            if (question == null) {
                Text("Preparando la actividad...", style = MaterialTheme.typography.bodyLarge)
            } else {
                Text(
                    "Pregunta ${state.index + 1} de ${state.total}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))
                SectionCard {
                    Text(
                        text = question.text,
                        style = MaterialTheme.typography.displayLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.weight(1f))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    question.options.chunked(2).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            row.forEach { option ->
                                AnswerButton(
                                    text = option.toString(),
                                    correct = state.reveal && option == question.answer,
                                    wrong = state.reveal && state.selected == option && option != question.answer,
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
