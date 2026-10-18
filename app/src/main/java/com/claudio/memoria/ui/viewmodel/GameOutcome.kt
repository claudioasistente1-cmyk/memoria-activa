package com.claudio.memoria.ui.viewmodel

data class GameOutcome(
    val correct: Int,
    val total: Int,
    val durationMs: Long
)

enum class GamePhase {
    SHOWING,
    INPUT,
    MEMORIZE,
    RECALL,
    FINISHED
}
