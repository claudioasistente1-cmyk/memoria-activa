package com.claudio.memoria.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.claudio.memoria.data.repository.ProgressRepository
import com.claudio.memoria.domain.daily.DailyRoutineGenerator
import com.claudio.memoria.domain.model.DailyRoutine
import com.claudio.memoria.util.TimeUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(repository: ProgressRepository) : ViewModel() {

    val routine: StateFlow<DailyRoutine> = repository.observeTodayRoutine()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DailyRoutineGenerator.generate(TimeUtils.today())
        )
}
