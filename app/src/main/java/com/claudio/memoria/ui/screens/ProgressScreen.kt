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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.claudio.memoria.domain.model.GameType
import com.claudio.memoria.ui.components.EmojiBadge
import com.claudio.memoria.ui.components.LevelPill
import com.claudio.memoria.ui.components.ScreenTitle
import com.claudio.memoria.ui.components.SectionCard
import com.claudio.memoria.ui.viewmodel.ProgressViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dayFormatter = DateTimeFormatter.ofPattern("d MMM", Locale.forLanguageTag("es-AR"))

@Composable
fun ProgressScreen(viewModel: ProgressViewModel) {
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()
    val totalPlayed by viewModel.totalGamesPlayed.collectAsStateWithLifecycle()
    val activeDays by viewModel.activeDays.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        ScreenTitle("Progreso")
        Text(
            "Tu actividad queda guardada solo en este telefono.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            SummaryCard(
                value = totalPlayed.toString(),
                label = "Juegos jugados",
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                value = activeDays.toString(),
                label = "Dias activos",
                modifier = Modifier.weight(1f)
            )
        }

        SectionCard {
            Text("Por juego", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))
            if (stats.isEmpty()) {
                Text(
                    "Todavia no jugaste. Cuando termines un juego vas a ver tus resultados aca.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                stats.forEachIndexed { index, stat ->
                    val type = GameType.fromId(stat.gameId) ?: return@forEachIndexed
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        EmojiBadge(type.emoji, size = 40)
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(type.title, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "${stat.timesPlayed} veces jugado",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        LevelPill(stat.bestLevel.coerceAtLeast(1))
                    }
                    if (index != stats.lastIndex) {
                        Spacer(Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }

        SectionCard {
            Text("Ultimos dias", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))
            if (history.isEmpty()) {
                Text(
                    "Cuando completes actividades vas a ver tu historial aca.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                history.take(14).forEachIndexed { index, day ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            day.date.format(dayFormatter),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "${day.completedCount} de ${day.totalTasks}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (index != history.take(14).lastIndex) {
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(value: String, label: String, modifier: Modifier = Modifier) {
    SectionCard(modifier = modifier) {
        Text(value, style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
