package com.claudio.memoria.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.claudio.memoria.MemoriaApp

class UpdateCheckWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val container = (applicationContext as MemoriaApp).container
        runCatching { container.contentRepository.refresh() }
        runCatching { container.updateManager.checkForUpdate(notifyIfNew = true) }
        return Result.success()
    }
}
