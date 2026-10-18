package com.claudio.memoria.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.claudio.memoria.domain.engine.SequenceEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

data class SequenceUiState(
    val phase: GamePhase = GamePhase.SHOWING,
    val length: Int = 0,
    val paletteSize: Int = 4,
    val highlighted: Int? = null,
    val tapIndex: Int = 0,
    val correctCount: Int = 0
)

class SequenceViewModel(
    private val level: Int,
    private val paletteSize: Int,
    private val random: Random = Random.Default
) : ViewModel() {

    private val _state = MutableStateFlow(SequenceUiState(paletteSize = paletteSize))
    val state: StateFlow<SequenceUiState> = _state.asStateFlow()

    private val _outcome = MutableStateFlow<GameOutcome?>(null)
    val outcome: StateFlow<GameOutcome?> = _outcome.asStateFlow()

    private var sequence: List<Int> = emptyList()
    private var playbackJob: Job? = null
    private var startedAt = System.currentTimeMillis()

    init {
        start()
    }

    private fun start() {
        playbackJob?.cancel()
        startedAt = System.currentTimeMillis()
        sequence = SequenceEngine.generate(level, paletteSize, random)
        _state.value = SequenceUiState(
            phase = GamePhase.SHOWING,
            length = sequence.size,
            paletteSize = paletteSize
        )
        playbackJob = viewModelScope.launch {
            delay(700)
            for (color in sequence) {
                _state.value = _state.value.copy(highlighted = color)
                delay(520)
                _state.value = _state.value.copy(highlighted = null)
                delay(220)
            }
            _state.value = _state.value.copy(phase = GamePhase.INPUT)
        }
    }

    fun onColorTap(color: Int) {
        val current = _state.value
        if (current.phase != GamePhase.INPUT) return
        val expected = sequence.getOrNull(current.tapIndex) ?: return

        if (color != expected) {
            _state.value = current.copy(phase = GamePhase.FINISHED)
            _outcome.value = GameOutcome(
                correct = current.correctCount,
                total = sequence.size,
                durationMs = System.currentTimeMillis() - startedAt
            )
            return
        }

        val nextIndex = current.tapIndex + 1
        if (nextIndex >= sequence.size) {
            _state.value = current.copy(
                phase = GamePhase.FINISHED,
                tapIndex = nextIndex,
                correctCount = sequence.size
            )
            _outcome.value = GameOutcome(
                correct = sequence.size,
                total = sequence.size,
                durationMs = System.currentTimeMillis() - startedAt
            )
        } else {
            _state.value = current.copy(tapIndex = nextIndex, correctCount = nextIndex)
        }
    }

    fun restart() {
        _outcome.value = null
        start()
    }

    override fun onCleared() {
        playbackJob?.cancel()
        super.onCleared()
    }
}
