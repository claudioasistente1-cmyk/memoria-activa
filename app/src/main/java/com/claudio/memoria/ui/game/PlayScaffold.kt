package com.claudio.memoria.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.claudio.memoria.ui.components.BigButton
import com.claudio.memoria.ui.components.LevelPill
import com.claudio.memoria.ui.theme.Danger
import com.claudio.memoria.ui.theme.Success

@Composable
fun PlayScaffold(
    title: String,
    level: Int,
    onBack: () -> Unit,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(56.dp)) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    modifier = Modifier.size(30.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            LevelPill(level)
        }
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            content = content
        )
    }
}

@Composable
fun FinishedOverlay(
    visible: Boolean,
    correct: Int,
    total: Int,
    onRepeat: () -> Unit,
    onExit: () -> Unit
) {
    if (!visible) return
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x66000000)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = resultMessage(correct, total),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (total > 0) "Acertaste $correct de $total" else "Terminaste la actividad",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                BigButton(
                    text = "Jugar de nuevo",
                    onClick = onRepeat,
                    modifier = Modifier.fillMaxWidth()
                )
                BigButton(
                    text = "Volver",
                    onClick = onExit,
                    modifier = Modifier.fillMaxWidth(),
                    outlined = true
                )
            }
        }
    }
}

@Composable
fun AnswerButton(
    text: String,
    correct: Boolean = false,
    wrong: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val container = when {
        correct -> Success
        wrong -> Danger
        else -> MaterialTheme.colorScheme.primaryContainer
    }
    val content = if (correct || wrong) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
    BigButton(
        text = text,
        onClick = onClick,
        modifier = modifier.heightIn(min = 76.dp),
        containerColor = container,
        contentColor = content,
        enabled = enabled
    )
}

fun resultMessage(correct: Int, total: Int): String {
    if (total <= 0) return "Terminaste"
    val accuracy = correct.toFloat() / total
    return when {
        accuracy >= 0.99f -> "Excelente"
        accuracy >= 0.7f -> "Muy bien"
        accuracy >= 0.4f -> "Buen trabajo"
        else -> "Seguimos practicando"
    }
}
