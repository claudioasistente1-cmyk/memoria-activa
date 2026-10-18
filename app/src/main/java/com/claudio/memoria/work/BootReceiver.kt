package com.claudio.memoria.work

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.claudio.memoria.data.prefs.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                val pending = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val settings = SettingsRepository(context).settings.first()
                        WorkScheduler.scheduleDailyReminder(
                            context,
                            settings.reminderHour,
                            settings.reminderMinute,
                            settings.reminderEnabled
                        )
                        WorkScheduler.scheduleUpdateCheck(context)
                    } finally {
                        pending.finish()
                    }
                }
            }
        }
    }
}
