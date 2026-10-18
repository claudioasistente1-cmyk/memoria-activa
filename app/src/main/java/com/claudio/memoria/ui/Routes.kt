package com.claudio.memoria.ui

object Routes {
    const val HOME = "home"
    const val GAMES = "games"
    const val DAILY = "daily"
    const val PROGRESS = "progress"
    const val SETTINGS = "settings"
    const val UPDATE = "update"
    const val PLAY = "play/{gameId}?level={level}"

    fun play(gameId: String, level: Int): String = "play/$gameId?level=$level"
}
