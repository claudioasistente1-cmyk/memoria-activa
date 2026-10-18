package com.claudio.memoria.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.claudio.memoria.domain.engine.MathEngine
import com.claudio.memoria.domain.engine.MathQuestion
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

data class MathUiState(
    val question: MathQuestion? = null,
    val index: Int = 0,
    val total: Int = 0,
    val correct: Int = 0,
    val selected: Int? = null,
    val reveal: Boolean = false,
    val finished: Boolean = false
)

class MathViewModel(
    private val level: Int,
    private val customQuestions: List<MathQuestion> = emptyList(),
    private val random: Random = Random.Default
) : ViewModel() {

    private val questions: List<MathQuestion> = buildList {
        addAll(customQuestions.take(5))
        while (size < 5) add(MathEngine.generate(level, random))
    }

    private val _state = MutableStateFlow(
        MathUiState(question = questions.first(), total = questions.size)
    )
    val state: StateFlow<MathUiState> = _state.asStateFlow()

    private val _outcome = MutableStateFlow<GameOutcome?>(null)
    val outcome: StateFlow<GameOutcome?> = _outcome.asStateFlow()

    private var startedAt = System.currentTimeMillis()
    private var session = 0

    fun answer(option: Int) {
        val current = _state.value
        if (current.finished || current.reveal) return
        val question = questions.getOrNull(current.index) ?: return
        val isCorrect = option == question.answer
        val correct = current.correct + if (isCorrect) 1 else 0
        _state.value = current.copy(selected = option, reveal = true, correct = correct)
        val sessionAtAnswer = session

        viewModelScope.launch {
            delay(1100)
            if (session != sessionAtAnswer) return@launch
            val nextIndex = current.index + 1
            if (nextIndex >= questions.size) {
                _state.value = _state.value.copy(finished = true)
                _outcome.value = GameOutcome(
                    correct = correct,
                    total = questions.size,
                    durationMs = System.currentTimeMillis() - startedAt
                )
            } else {
                _state.value = MathUiState(
                    question = questions[nextIndex],
                    index = nextIndex,
                    total = questions.size,
                    correct = correct
                )
            }
        }
    }

    fun restart() {
        session++
        startedAt = System.currentTimeMillis()
        _outcome.value = null
        _state.value = MathUiState(question = questions.first(), total = questions.size)
    }
}
