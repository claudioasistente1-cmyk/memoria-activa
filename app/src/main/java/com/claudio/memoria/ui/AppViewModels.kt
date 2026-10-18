package com.claudio.memoria.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.claudio.memoria.di.AppContainer
import com.claudio.memoria.ui.viewmodel.DailyViewModel
import com.claudio.memoria.ui.viewmodel.GamesViewModel
import com.claudio.memoria.ui.viewmodel.HomeViewModel
import com.claudio.memoria.ui.viewmodel.ProgressViewModel
import com.claudio.memoria.ui.viewmodel.SettingsViewModel
import com.claudio.memoria.ui.viewmodel.UpdateViewModel

fun appViewModelFactory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
    initializer { HomeViewModel(container.progressRepository) }
    initializer { DailyViewModel(container.progressRepository) }
    initializer { GamesViewModel(container.progressRepository) }
    initializer { ProgressViewModel(container.progressRepository) }
    initializer {
        SettingsViewModel(
            settingsRepository = container.settingsRepository,
            appContext = container.appContext
        )
    }
    initializer { UpdateViewModel(container.updateManager) }
}
