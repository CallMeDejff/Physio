package com.example.physio.screens.reminders.components

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.physio.R

class ReminderWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val topic = inputData.getString("topic") ?: "Twoja sesja"

        Log.d("ReminderWorker", "Powiadomienie: $topic")

        sendNotification("Czas na ćwiczenia!", "Nie zapomnij o swojej sesji z $topic!")
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


