package com.claudio.memoria.di

import android.content.Context
import com.claudio.memoria.data.content.ContentRepository
import com.claudio.memoria.data.db.AppDatabase
import com.claudio.memoria.data.prefs.SettingsRepository
import com.claudio.memoria.data.remote.UpdateRepository
import com.claudio.memoria.data.repository.ProgressRepository
import com.claudio.memoria.update.ApkInstaller
import com.claudio.memoria.update.UpdateManager
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class AppContainer(context: Context) {

    val appContext: Context = context.applicationContext

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val moshi: Moshi = Moshi.Builder().build()

    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val database = AppDatabase.get(appContext)

    val progressRepository = ProgressRepository(database.progressDao())
    val settingsRepository = SettingsRepository(appContext)
    val contentRepository = ContentRepository(appContext, moshi, okHttpClient)
    val updateRepository = UpdateRepository(appContext, moshi, okHttpClient)
    val apkInstaller = ApkInstaller(appContext)
    val updateManager = UpdateManager(
        context = appContext,
        repository = updateRepository,
        settings = settingsRepository,
        installer = apkInstaller
    )
}
