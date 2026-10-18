package com.claudio.memoria.domain.engine

import kotlin.random.Random

data class PairsCard(
    val id: Int,
    val pairId: Int,
    val symbol: String
)

object PairsEngine {

    fun pairCountFor(level: Int): Int = when (level.coerceIn(1, 8)) {
        1 -> 3
        2 -> 4
        3 -> 6
        4 -> 8
        else -> 10
    }

    fun generateDeck(level: Int, symbolPool: List<String>, random: Random = Random.Default): List<PairsCard> {
        val pool = symbolPool.ifEmpty { listOf("\uD83C\uDF4E", "\uD83C\uDF4C", "\uD83D\uDD14", "\u2B50", "\uD83C\uDF3B") }
        val pairCount = pairCountFor(level).coerceAtMost(pool.size)
        val symbols = pool.shuffled(random).take(pairCount)
        val cards = mutableListOf<PairsCard>()
        symbols.forEachIndexed { pairId, symbol ->
            cards.add(PairsCard(cards.size, pairId, symbol))
            cards.add(PairsCard(cards.size, pairId, symbol))
        }
        return cards.shuffled(random)
    }
}
