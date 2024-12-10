package com.dawidkubica.physio.screens.reminders.components

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dawidkubica.physio.R

class ReminderWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val topic = inputData.getString("topic")
            ?: applicationContext.getString(R.string.default_session_topic)

        Log.d("ReminderWorker", "Powiadomienie: $topic")

        val title = applicationContext.getString(R.string.notification_title)
        val message = applicationContext.getString(R.string.notification_message, topic)

        sendNotification(title, message)
        return Result.success()
    }

    private fun sendNotification(title: String, message: String) {
        Log.d("ReminderWorker", "Sending notification: $title, $message")

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(applicationContext, "REMINDER_CHANNEL")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.logo_clear)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(1, notification)
    }
}


