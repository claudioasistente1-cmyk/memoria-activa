package com.claudio.memoria.notifications

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.claudio.memoria.MainActivity
import com.claudio.memoria.R
import com.claudio.memoria.data.remote.UpdateManifest

object NotificationHelper {

    const val EXTRA_DESTINATION = "destination"
    const val DESTINATION_DAILY = "daily"
    const val DESTINATION_UPDATE = "update"

    const val CHANNEL_REMINDERS = "reminders"
    const val CHANNEL_UPDATES = "updates"

    const val ID_DAILY_REMINDER = 1001
    const val ID_UPDATE_AVAILABLE = 2001
    const val ID_DOWNLOADING = 2002

    fun createChannels(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_REMINDERS,
                context.getString(R.string.channel_reminders),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.channel_reminders_desc)
            }
        )
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_UPDATES,
                context.getString(R.string.channel_updates),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.channel_updates_desc)
            }
        )
    }

    fun canNotify(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun openAppPendingIntent(
        context: Context,
        destination: String,
        requestCode: Int
    ): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_DESTINATION, destination)
        }
        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun builder(context: Context, channel: String): Notification.Builder =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, channel)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(context)
        }

    fun showDailyReminder(context: Context, message: String) {
        if (!canNotify(context)) return
        val notification = builder(context, CHANNEL_REMINDERS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notif_reminder_title))
            .setContentText(message)
            .setStyle(Notification.BigTextStyle().bigText(message))
            .setAutoCancel(true)
            .setContentIntent(
                openAppPendingIntent(context, DESTINATION_DAILY, ID_DAILY_REMINDER)
            )
            .build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(ID_DAILY_REMINDER, notification)
    }

    fun showUpdateAvailable(context: Context, manifest: UpdateManifest) {
        if (!canNotify(context)) return
        val text = context.getString(R.string.notif_update_text, manifest.versionName)
        val notification = builder(context, CHANNEL_UPDATES)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notif_update_title))
            .setContentText(text)
            .setStyle(Notification.BigTextStyle().bigText(manifest.releaseNotes.ifBlank { text }))
            .setAutoCancel(true)
            .setContentIntent(openAppPendingIntent(context, DESTINATION_UPDATE, ID_UPDATE_AVAILABLE))
            .build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(ID_UPDATE_AVAILABLE, notification)
    }

    fun showDownloading(context: Context, versionName: String) {
        if (!canNotify(context)) return
        val notification = builder(context, CHANNEL_UPDATES)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notif_downloading_title))
            .setContentText(context.getString(R.string.notif_downloading_text, versionName))
            .setOngoing(true)
            .setProgress(0, 0, true)
            .build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(ID_DOWNLOADING, notification)
    }

    fun cancelDownloading(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(ID_DOWNLOADING)
    }

    fun cancelUpdateAvailable(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(ID_UPDATE_AVAILABLE)
    }
}
