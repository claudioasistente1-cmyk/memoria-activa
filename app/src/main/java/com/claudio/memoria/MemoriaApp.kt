package com.claudio.memoria

import android.app.Application
import com.claudio.memoria.data.prefs.SettingsRepository
import com.claudio.memoria.di.AppContainer
import com.claudio.memoria.notifications.NotificationHelper
import com.claudio.memoria.work.WorkScheduler
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MemoriaApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        NotificationHelper.createChannels(this)

        container.applicationScope.launch {
            val settings = SettingsRepository(this@MemoriaApp).settings.first()
            WorkScheduler.scheduleDailyReminder(
                this@MemoriaApp,
                settings.reminderHour,
                settings.reminderMinute,
                settings.reminderEnabled
            )
            WorkScheduler.scheduleUpdateCheck(this@MemoriaApp)
            runCatching { container.contentRepository.refresh() }
            runCatching { container.updateManager.checkForUpdate(notifyIfNew = true) }
        }
    }
}
