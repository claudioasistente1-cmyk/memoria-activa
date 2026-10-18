package com.claudio.memoria.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.claudio.memoria.data.db.AppDatabase
import com.claudio.memoria.data.prefs.SettingsRepository
import com.claudio.memoria.domain.daily.DailyRoutineGenerator
import com.claudio.memoria.domain.model.GameType
import com.claudio.memoria.notifications.NotificationHelper
import com.claudio.memoria.util.TimeUtils
import kotlinx.coroutines.flow.first
import java.time.LocalTime

class DailyReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val settings = SettingsRepository(applicationContext).settings.first()
        if (!settings.reminderEnabled) return Result.success()

        val today = TimeUtils.today()
        val dao = AppDatabase.get(applicationContext).progressDao()
        val completed = dao.getCompletions(today.toString())
            .mapNotNull { GameType.fromId(it.gameId) }
            .toSet()

        val routine = DailyRoutineGenerator.generate(today, completedGames = completed)
        if (routine.isComplete) return Result.success()

        val hour = LocalTime.now().hour
        val message = when {
            hour < 13 -> "\u00A1Buen dia! Tus ejercicios de memoria estan listos."
            hour < 19 -> "\u00BFJugamos 10 minutos? Hoy tenes nuevos desafios."
            else -> "Todavia podes completar tus juegos de hoy."
        }
        NotificationHelper.showDailyReminder(applicationContext, message)
        return Result.success()
    }
}
