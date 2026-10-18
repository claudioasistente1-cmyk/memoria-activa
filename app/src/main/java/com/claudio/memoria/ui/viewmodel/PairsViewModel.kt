package com.claudio.memoria.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.claudio.memoria.domain.engine.PairsCard
import com.claudio.memoria.domain.engine.PairsEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

data class PairsUiState(
    val cards: List<PairsCard> = emptyList(),
    val faceUp: List<Int> = emptyList(),
    val matchedPairs: Set<Int> = emptySet(),
    val attempts: Int = 0,
    val finished: Boolean = false
) {
    val pairCount: Int get() = cards.size / 2
    fun isVisible(cardId: Int): Boolean =
        cardId in faceUp || (cards.getOrNull(cardId)?.let { it.pairId in matchedPairs } == true)
}

class PairsViewModel(
    private val level: Int,
    private val symbols: List<String>,
    private val random: Random = Random.Default
) : ViewModel() {

    private val _state = MutableStateFlow(PairsUiState(cards = PairsEngine.generateDeck(level, symbols, random)))
    val state: StateFlow<PairsUiState> = _state.asStateFlow()

    private val _outcome = MutableStateFlow<GameOutcome?>(null)
    val outcome: StateFlow<GameOutcome?> = _outcome.asStateFlow()

    private var startedAt = System.currentTimeMillis()
    private var session = 0

    fun flip(cardId: Int) {
        val current = _state.value
        if (current.finished || current.faceUp.size >= 2) return
        if (cardId in current.faceUp) return
        val card = current.cards.getOrNull(cardId) ?: return
        if (card.pairId in current.matchedPairs) return

        if (current.faceUp.isEmpty()) {
            _state.value = current.copy(faceUp = listOf(cardId))
            return
        }

        val firstCard = current.cards[current.faceUp.first()]
        val isMatch = firstCard.pairId == card.pairId
        val attempts = current.attempts + 1
        _state.value = current.copy(faceUp = current.faceUp + cardId, attempts = attempts)
        val sessionAtFlip = session

        viewModelScope.launch {
            delay(if (isMatch) 420 else 850)
            if (session != sessionAtFlip) return@launch
            val latest = _state.value
            if (isMatch) {
                val matched = latest.matchedPairs + card.pairId
                val finished = matched.size >= latest.pairCount
                _state.value = latest.copy(faceUp = emptyList(), matchedPairs = matched, finished = finished)
                if (finished) {
                    _outcome.value = GameOutcome(
                        correct = latest.pairCount,
                        total = attempts.coerceAtLeast(latest.pairCount),
                        durationMs = System.currentTimeMillis() - startedAt
                    )
                }
            } else {
                _state.value = latest.copy(faceUp = emptyList())
            }
        }
    }

    fun restart() {
        session++
        startedAt = System.currentTimeMillis()
        _outcome.value = null
        _state.value = PairsUiState(cards = PairsEngine.generateDeck(level, symbols, random))
    }
}
