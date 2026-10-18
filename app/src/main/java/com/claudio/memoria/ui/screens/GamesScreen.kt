package com.claudio.memoria.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.claudio.memoria.domain.model.GameType
import com.claudio.memoria.ui.components.BigButton
import com.claudio.memoria.ui.components.EmojiBadge
import com.claudio.memoria.ui.components.LevelPill
import com.claudio.memoria.ui.components.ScreenTitle
import com.claudio.memoria.ui.components.SectionCard
import com.claudio.memoria.ui.viewmodel.GamesViewModel

@Composable
fun GamesScreen(
    viewModel: GamesViewModel,
    onPlay: (GameType, Int) -> Unit
) {
    val levels by viewModel.levels.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        ScreenTitle("Juegos")
        Text(
            "Elegí un juego para entrenar. Podés repetirlo las veces que quieras.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        GameType.values().forEach { type ->
            val level = levels[type] ?: 1
            SectionCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    EmojiBadge(type.emoji, size = 52)
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(type.title, style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            type.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LevelPill(level)
                    Spacer(Modifier.weight(1f))
                    BigButton(
                        text = "Jugar",
                        onClick = { onPlay(type, level) },
                        icon = Icons.Filled.PlayArrow
                    )
                }
            }
        }
    }
}
