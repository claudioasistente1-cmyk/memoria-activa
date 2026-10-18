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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.claudio.memoria.ui.components.BigButton
import com.claudio.memoria.ui.components.SectionCard
import com.claudio.memoria.ui.viewmodel.HomeViewModel
import java.time.LocalTime

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onPlayDaily: () -> Unit,
    onOpenGames: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val routine by viewModel.routine.collectAsStateWithLifecycle()
    val greeting = when (LocalTime.now().hour) {
        in 5..12 -> "Buen día"
        in 13..19 -> "Buenas tardes"
        else -> "Buenas noches"
    }
    val total = routine.tasks.size
    val completed = routine.completedCount
    val fraction = if (total > 0) completed.toFloat() / total else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(greeting, style = MaterialTheme.typography.headlineLarge)
                Text(
                    "Entrenemos la memoria juntos",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onOpenSettings, modifier = Modifier.size(56.dp)) {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Ajustes",
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        SectionCard {
            Text("Desafío de hoy", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text(
                "$completed de $total actividades",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(14.dp))
            LinearProgressIndicator(
                progress = { fraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp),
            )
            Spacer(Modifier.height(12.dp))
            Text(
                if (routine.isComplete) {
                    "Completaste tus juegos de hoy. Muy bien."
                } else {
                    "Te faltan ${total - completed} actividades para terminar."
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }

        BigButton(
            text = "Jugar",
            onClick = onPlayDaily,
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Filled.PlayArrow
        )

        BigButton(
            text = "Ver todos los juegos",
            onClick = onOpenGames,
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Filled.SportsEsports,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )

        SectionCard {
            Text("Un poco cada dia", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Text(
                "Dedicar unos minutos a estos ejercicios ayuda a mantener la mente activa. " +
                    "No hay apuro: podes repetir los juegos las veces que quieras.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
