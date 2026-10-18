package com.claudio.memoria.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.claudio.memoria.domain.model.DailyTask
import com.claudio.memoria.domain.model.GameType
import com.claudio.memoria.ui.components.BigButton
import com.claudio.memoria.ui.components.EmojiBadge
import com.claudio.memoria.ui.components.LevelPill
import com.claudio.memoria.ui.components.ScreenTitle
import com.claudio.memoria.ui.components.SectionCard
import com.claudio.memoria.ui.viewmodel.DailyViewModel

@Composable
fun DailyScreen(
    viewModel: DailyViewModel,
    onPlay: (GameType, Int) -> Unit
) {
    val routine by viewModel.routine.collectAsStateWithLifecycle()
    val total = routine.tasks.size
    val completed = routine.completedCount

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        ScreenTitle("Desafío de hoy")
        Text(
            "Tres actividades cortas para hacer sin apuro.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        SectionCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "$completed de $total completadas",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    if (routine.isComplete) "Listo" else "En curso",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { if (total > 0) completed.toFloat() / total else 0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp),
            )
        }

        routine.tasks.forEach { task ->
            DailyTaskCard(task = task, onPlay = { onPlay(task.gameType, task.level) })
        }

        SectionCard {
            Text("Sin presión", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Text(
                "Si un día no podés jugar, no pasa nada. Tu progreso queda guardado y " +
                    "mañana tendrás nuevos desafíos.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DailyTaskCard(task: DailyTask, onPlay: () -> Unit) {
    SectionCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            EmojiBadge(task.gameType.emoji)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(task.gameType.title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LevelPill(task.level)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        if (task.completed) "Completada" else "Pendiente",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = if (task.completed) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (task.completed) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(34.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        BigButton(
            text = if (task.completed) "Jugar de nuevo" else "Jugar",
            onClick = onPlay,
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Filled.PlayArrow,
            containerColor = if (task.completed) MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.primary,
            contentColor = if (task.completed) MaterialTheme.colorScheme.onSecondaryContainer
            else MaterialTheme.colorScheme.onPrimary
        )
    }
}
