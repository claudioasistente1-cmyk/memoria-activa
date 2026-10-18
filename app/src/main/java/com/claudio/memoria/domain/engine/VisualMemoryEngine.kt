package com.claudio.memoria.domain.engine

import kotlin.random.Random

data class VisualMemoryRound(
    val gridSize: Int,
    val items: Map<Int, String>
) {
    val targetCells: Set<Int> get() = items.keys
}

object VisualMemoryEngine {

    fun generate(level: Int, symbolPool: List<String>, random: Random = Random.Default): VisualMemoryRound {
        val lvl = level.coerceIn(1, 8)
        val grid = if (lvl <= 3) 3 else 4
        val cells = grid * grid
        val count = ((lvl / 2) + 2).coerceIn(2, minOf(cells - 1, 8))
        val pool = symbolPool.ifEmpty { listOf("\u2B50", "\uD83C\uDF4E", "\uD83C\uDF3B", "\uD83D\uDD14", "\uD83C\uDF40") }
        val chosenCells = (0 until cells).shuffled(random).take(count)
        val symbols = pool.shuffled(random).take(count)
        val items = chosenCells.withIndex().associate { (i, cell) -> cell to symbols[i % symbols.size] }
        return VisualMemoryRound(grid, items)
    }
}
