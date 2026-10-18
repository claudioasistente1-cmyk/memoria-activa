package com.claudio.memoria.update

import android.content.Context
import com.claudio.memoria.BuildConfig
import com.claudio.memoria.data.prefs.SettingsRepository
import com.claudio.memoria.data.remote.UpdateManifest
import com.claudio.memoria.data.remote.UpdateRepository
import com.claudio.memoria.notifications.NotificationHelper
import kotlinx.coroutines.flow.first
import java.io.File

sealed interface UpdateCheckResult {
    data class Available(val manifest: UpdateManifest) : UpdateCheckResult
    data object UpToDate : UpdateCheckResult
    data object Failed : UpdateCheckResult
}

sealed interface DownloadResult {
    data class Ready(val file: File) : DownloadResult
    data object HashMismatch : DownloadResult
    data object PermissionRequired : DownloadResult
    data class Failed(val message: String) : DownloadResult
}

class UpdateManager(
    private val context: Context,
    private val repository: UpdateRepository,
    private val settings: SettingsRepository,
    private val installer: ApkInstaller
) {
    suspend fun checkForUpdate(notifyIfNew: Boolean): UpdateCheckResult {
        val manifest = repository.fetchManifest() ?: return UpdateCheckResult.Failed
        if (!manifest.isUsable() || manifest.versionCode <= BuildConfig.VERSION_CODE) {
            return UpdateCheckResult.UpToDate
        }
        settings.setLastUpdateCheck(System.currentTimeMillis())
        if (notifyIfNew) {
            val lastNotified = settings.settings.first().lastNotifiedVersionCode
            if (lastNotified != manifest.versionCode) {
                NotificationHelper.showUpdateAvailable(context, manifest)
                settings.setLastNotifiedVersionCode(manifest.versionCode)
            }
        }
        return UpdateCheckResult.Available(manifest)
    }

    fun canInstallPackages(): Boolean = installer.canInstallPackages()

    fun openInstallPermission() = installer.openInstallPermissionSettings()

    suspend fun downloadAndInstall(
        manifest: UpdateManifest,
        onProgress: (Float) -> Unit = {}
    ): DownloadResult {
        if (!installer.canInstallPackages()) return DownloadResult.PermissionRequired
        NotificationHelper.showDownloading(context, manifest.versionName)
        return try {
            val file = repository.downloadApk(manifest, onProgress)
            val hash = repository.sha256(file)
            NotificationHelper.cancelDownloading(context)
            if (!hash.equals(manifest.sha256, ignoreCase = true)) {
                file.delete()
                DownloadResult.HashMismatch
            } else {
                installer.install(file)
                DownloadResult.Ready(file)
            }
        } catch (t: Throwable) {
            NotificationHelper.cancelDownloading(context)
            DownloadResult.Failed(t.message ?: "Error de descarga")
        }
    }
}
