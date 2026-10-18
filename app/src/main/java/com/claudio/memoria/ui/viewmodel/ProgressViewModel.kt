package com.claudio.memoria.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.claudio.memoria.data.db.GameResultEntity
import com.claudio.memoria.data.db.GameStat
import com.claudio.memoria.data.repository.DayHistory
import com.claudio.memoria.data.repository.ProgressRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ProgressViewModel(repository: ProgressRepository) : ViewModel() {

    val stats: StateFlow<List<GameStat>> = repository.observeStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val recent: StateFlow<List<GameResultEntity>> = repository.observeRecentResults()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val history: StateFlow<List<DayHistory>> = repository.observeHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val totalGamesPlayed: StateFlow<Int> = repository.observeTotalGamesPlayed()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val activeDays: StateFlow<Int> = repository.observeActiveDays()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
}
