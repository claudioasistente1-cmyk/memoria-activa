package com.claudio.memoria.ui.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.claudio.memoria.data.content.GameContent
import com.claudio.memoria.di.AppContainer
import com.claudio.memoria.domain.engine.MathEngine
import com.claudio.memoria.domain.engine.MathQuestion
import com.claudio.memoria.domain.engine.SeriesEngine
import com.claudio.memoria.domain.engine.SeriesQuestion
import com.claudio.memoria.domain.model.GameType
import com.claudio.memoria.ui.components.BigButton
import com.claudio.memoria.ui.sound.SoundEffects
import com.claudio.memoria.ui.viewmodel.GameOutcome
import com.claudio.memoria.ui.viewmodel.LogicViewModel
import com.claudio.memoria.ui.viewmodel.MathViewModel
import com.claudio.memoria.ui.viewmodel.PairsViewModel
import com.claudio.memoria.ui.viewmodel.SequenceViewModel
import com.claudio.memoria.ui.viewmodel.VisualMemoryViewModel
import kotlinx.coroutines.launch

@Composable
fun PlayScreen(
    container: AppContainer,
    gameId: String,
    level: Int,
    soundEnabled: Boolean,
    onExit: () -> Unit
) {
    val gameType = GameType.fromId(gameId)
    val content by container.contentRepository.content.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val safeLevel = level.coerceIn(1, 8)

    val soundEnabledState = rememberUpdatedState(soundEnabled)
    val sound = remember { SoundEffects { soundEnabledState.value } }
    DisposableEffect(sound) {
        onDispose { sound.release() }
    }

    val record: (GameOutcome) -> Unit = { outcome ->
        val type = gameType
        if (type != null) {
            scope.launch {
                container.progressRepository.recordGame(
                    gameType = type,
                    correct = outcome.correct,
                    total = outcome.total,
                    durationMs = outcome.durationMs,
                    level = safeLevel
                )
            }
        }
    }

    if (gameType == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Este juego no esta disponible.", style = MaterialTheme.typography.titleLarge)
            BigButton("Volver", onExit, Modifier.fillMaxWidth())
        }
        return
    }

    when (gameType) {
        GameType.PAIRS -> {
            val vm: PairsViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { PairsViewModel(safeLevel, content.pairs.symbols) }
                }
            )
            val state by vm.state.collectAsStateWithLifecycle()
            val outcome by vm.outcome.collectAsStateWithLifecycle()
            LaunchedEffect(outcome) { outcome?.let(record) }
            PairsGame(
                level = safeLevel,
                state = state,
                sound = sound,
                onFlip = vm::flip,
                onRepeat = vm::restart,
                onExit = onExit
            )
        }

        GameType.SEQUENCE -> {
            val paletteSize = content.sequence.paletteSize.coerceIn(2, 4)
            val vm: SequenceViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { SequenceViewModel(safeLevel, paletteSize) }
                }
            )
            val state by vm.state.collectAsStateWithLifecycle()
            val outcome by vm.outcome.collectAsStateWithLifecycle()
            LaunchedEffect(outcome) { outcome?.let(record) }
            SequenceGame(
                level = safeLevel,
                state = state,
                colors = content.sequence.colors,
                sound = sound,
                onColorTap = vm::onColorTap,
                onRepeat = vm::restart,
                onExit = onExit
            )
        }

        GameType.VISUAL_MEMORY -> {
            val vm: VisualMemoryViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { VisualMemoryViewModel(safeLevel, content.visualMemory.symbols) }
                }
            )
            val state by vm.state.collectAsStateWithLifecycle()
            val outcome by vm.outcome.collectAsStateWithLifecycle()
            LaunchedEffect(outcome) { outcome?.let(record) }
            VisualMemoryGame(
                level = safeLevel,
                state = state,
                sound = sound,
                onToggleCell = vm::toggleCell,
                onSubmit = vm::submit,
                onRepeat = vm::restart,
                onExit = onExit
            )
        }

        GameType.MATH -> {
            val custom = remember(content, safeLevel) { buildMathQuestions(content, safeLevel) }
            val vm: MathViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { MathViewModel(safeLevel, custom) }
                }
            )
            val state by vm.state.collectAsStateWithLifecycle()
            val outcome by vm.outcome.collectAsStateWithLifecycle()
            LaunchedEffect(outcome) { outcome?.let(record) }
            MathGame(
                level = safeLevel,
                state = state,
                sound = sound,
                onAnswer = vm::answer,
                onRepeat = vm::restart,
                onExit = onExit
            )
        }

        GameType.LOGIC -> {
            val custom = remember(content, safeLevel) { buildSeriesQuestions(content, safeLevel) }
            val vm: LogicViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { LogicViewModel(safeLevel, custom) }
                }
            )
            val state by vm.state.collectAsStateWithLifecycle()
            val outcome by vm.outcome.collectAsStateWithLifecycle()
            LaunchedEffect(outcome) { outcome?.let(record) }
            LogicGame(
                level = safeLevel,
                state = state,
                sound = sound,
                onAnswer = vm::answer,
                onRepeat = vm::restart,
                onExit = onExit
            )
        }
    }
}

private fun buildMathQuestions(content: GameContent, level: Int): List<MathQuestion> {
    val levelContent = content.math.filter { it.level <= level }.maxByOrNull { it.level }
        ?: return emptyList()
    return levelContent.questions.mapNotNull { item ->
        if (item.text.isBlank()) return@mapNotNull null
        MathQuestion(
            text = item.text,
            answer = item.answer,
            options = sanitizeOptions(item.answer, item.options) { MathEngine.options(item.answer) }
        )
    }
}

private fun buildSeriesQuestions(content: GameContent, level: Int): List<SeriesQuestion> {
    val levelContent = content.series.filter { it.level <= level }.maxByOrNull { it.level }
        ?: return emptyList()
    return levelContent.items.mapNotNull { item ->
        if (item.sequence.isEmpty()) return@mapNotNull null
        val items = item.sequence.map { it as Int? } + listOf<Int?>(null)
        SeriesQuestion(
            items = items,
            missingIndex = items.lastIndex,
            answer = item.answer,
            options = sanitizeOptions(item.answer, item.options) { SeriesEngine.options(item.answer) }
        )
    }
}

private fun sanitizeOptions(
    answer: Int,
    provided: List<Int>,
    fallback: () -> List<Int>
): List<Int> {
    val options = linkedSetOf(answer)
    provided.forEach { if (options.size < 4) options.add(it) }
    fallback().forEach { if (options.size < 4) options.add(it) }
    var filler = answer + 3
    while (options.size < 4) {
        options.add(filler)
        filler += 3
    }
    return options.toList().shuffled()
}
