package com.jobik.shkiper.services.notification_service

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.Keep
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import com.jobik.shkiper.SharedPreferencesKeys
import com.jobik.shkiper.activity.MainActivity
import com.jobik.shkiper.database.models.RepeatMode
import com.jobik.shkiper.helpers.IntentHelper
import com.jobik.shkiper.services.statistics_service.StatisticsService
import com.jobik.shkiper.util.ThemeUtil
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime

@Keep
class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationScheduler = NotificationScheduler(context)
        when (intent.action) {
            Intent.ACTION_DATE_CHANGED -> {
                notificationScheduler.restoreNotifications()
            }

            Intent.ACTION_TIME_CHANGED -> {
                notificationScheduler.restoreNotifications()
            }

            Intent.ACTION_TIMEZONE_CHANGED -> {
                notificationScheduler.restoreNotifications()
            }

            Intent.ACTION_BOOT_COMPLETED -> {
                notificationScheduler.restoreNotifications()
            }

            else -> sendNotification(intent, context)
        }
    }

    private fun sendNotification(intent: Intent, context: Context) {
        val requestCode = intent.getIntExtra("requestCode", 0)
        val notification = NotificationStorage(context).getNotification(requestCode) ?: return

        ThemeUtil.restoreSavedTheme(context)
        val savedColors = ThemeUtil.getColors()

        // Create the TaskStackBuilder
        val mainPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(IntentHelper().getStartActivityAndOpenNoteIntent(context, notification.noteId))
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(
                notification.requestCode,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val notificationBuilder =
            NotificationCompat.Builder(context, notification.channel.channelId)
                .setSmallIcon(notification.icon)
                .setAutoCancel(true)
                .setColor(savedColors.active.toArgb())
                .setColorized(true)
                .setContentIntent(mainPendingIntent)
        if (notification.title.isNotEmpty())
            notificationBuilder.setContentTitle(notification.title)
        if (notification.message.isNotEmpty())
            notificationBuilder.setContentText(notification.message)
                .setStyle(
                    NotificationCompat
                        .BigTextStyle()
                        .bigText(notification.message)
                )

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notification.notificationId, notificationBuilder.build())

        scheduleRepeatableNotification(notification, context)

        // Statistics update
        val statisticsService = StatisticsService(context)
        statisticsService.appStatistics.apply {
            notificationCount.increment()
        }
        statisticsService.saveStatistics()
    }

    private fun scheduleRepeatableNotification(
        notification: NotificationData,
        context: Context
    ) {
        var newNotificationDate = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(notification.trigger),
            OffsetDateTime.now().offset
        )
        val notificationScheduler = NotificationScheduler(context)
        newNotificationDate = when (notification.repeatMode) {
            RepeatMode.NONE -> {
                notificationScheduler.deleteNotification(notification.requestCode)
                return
            }

            RepeatMode.DAILY -> {
                newNotificationDate.plusDays(1)
            }

            RepeatMode.WEEKLY -> {
                newNotificationDate.plusDays(7)
            }

            RepeatMode.MONTHLY -> {
                newNotificationDate.plusMonths(1)
            }

            RepeatMode.YEARLY -> {
                newNotificationDate.plusYears(1)
            }
        }

        val milliseconds = newNotificationDate.toInstant(OffsetDateTime.now().offset).toEpochMilli()
        notificationScheduler.scheduleNotification(
            notification.copy(trigger = milliseconds)
        )
    }
}