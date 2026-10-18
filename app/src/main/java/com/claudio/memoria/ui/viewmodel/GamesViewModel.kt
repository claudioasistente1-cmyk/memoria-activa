package com.claudio.memoria.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.claudio.memoria.data.db.GameStat
import com.claudio.memoria.data.repository.ProgressRepository
import com.claudio.memoria.data.repository.accuracy
import com.claudio.memoria.domain.model.GameType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class GamesViewModel(repository: ProgressRepository) : ViewModel() {

    val stats: StateFlow<List<GameStat>> = repository.observeStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val levels: StateFlow<Map<GameType, Int>> = repository.observeRecentResults()
        .map { results ->
            results.groupBy { it.gameId }.mapNotNull { (gameId, rows) ->
                val type = GameType.fromId(gameId) ?: return@mapNotNull null
                val latest = rows.maxByOrNull { it.timestamp } ?: return@mapNotNull null
                type to ProgressRepository.adaptLevel(latest.level, latest.accuracy())
            }.toMap()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())
}
