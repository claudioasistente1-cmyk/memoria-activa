package com.claudio.memoria.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.claudio.memoria.domain.engine.VisualMemoryEngine
import com.claudio.memoria.domain.engine.VisualMemoryRound
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

data class VisualMemoryUiState(
    val round: VisualMemoryRound = VisualMemoryRound(3, emptyMap()),
    val phase: GamePhase = GamePhase.MEMORIZE,
    val memorizeMillis: Long = 2500,
    val selected: Set<Int> = emptySet(),
    val correctCount: Int = 0,
    val wrongCount: Int = 0
) {
    val reveal: Boolean get() = phase == GamePhase.FINISHED
    val targetCount: Int get() = round.items.size
}

class VisualMemoryViewModel(
    private val level: Int,
    private val symbols: List<String>,
    private val random: Random = Random.Default
) : ViewModel() {

    private val _state = MutableStateFlow(VisualMemoryUiState())
    val state: StateFlow<VisualMemoryUiState> = _state.asStateFlow()

    private val _outcome = MutableStateFlow<GameOutcome?>(null)
    val outcome: StateFlow<GameOutcome?> = _outcome.asStateFlow()

    private var phaseJob: Job? = null
    private var startedAt = System.currentTimeMillis()

    init {
        start()
    }

    private fun start() {
        phaseJob?.cancel()
        startedAt = System.currentTimeMillis()
        val round = VisualMemoryEngine.generate(level, symbols, random)
        val memorizeMillis = (2200L + round.items.size * 450L).coerceAtMost(7000L)
        _state.value = VisualMemoryUiState(
            round = round,
            phase = GamePhase.MEMORIZE,
            memorizeMillis = memorizeMillis
        )
        phaseJob = viewModelScope.launch {
            delay(memorizeMillis)
            _state.value = _state.value.copy(phase = GamePhase.RECALL)
        }
    }

    fun toggleCell(cell: Int) {
        val current = _state.value
        if (current.phase != GamePhase.RECALL) return
        val selected = if (cell in current.selected) current.selected - cell else current.selected + cell
        _state.value = current.copy(selected = selected)
    }

    fun submit() {
        val current = _state.value
        if (current.phase != GamePhase.RECALL) return
        val target = current.round.targetCells
        val correct = current.selected.intersect(target).size
        val wrong = current.selected.minus(target).size
        _state.value = current.copy(
            phase = GamePhase.FINISHED,
            correctCount = correct,
            wrongCount = wrong
        )
        _outcome.value = GameOutcome(
            correct = correct,
            total = target.size.coerceAtLeast(1),
            durationMs = System.currentTimeMillis() - startedAt
        )
    }

    fun restart() {
        _outcome.value = null
        start()
    }

    override fun onCleared() {
        phaseJob?.cancel()
        super.onCleared()
    }
}
