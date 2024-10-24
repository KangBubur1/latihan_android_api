package com.example.submission_awal.workmanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.submission_awal.MainActivity
import com.example.submission_awal.data.network.ApiConfig
import com.example.submission_awal.data.response.ListEventsItem
import com.example.submission_awal.ui.setting.SettingPreferences
import com.example.submission_awal.ui.setting.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MyWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    private val channelId = "event_reminder_channel"
    private val notificationId = 1

    private val apiService = ApiConfig.getApiService()
    override fun doWork(): Result {
        Log.d("MyWorker", "Work started")


        val isNotificationEnabled = runBlocking {
            val settingPreferences = SettingPreferences.getInstance(applicationContext.dataStore)
            try {
                settingPreferences.isNotificationEnabled().first()
            } catch (e: Exception) {
                Log.e("MyWorker", "Failed to fetch notification preference", e)
                return@runBlocking false
            }
        }

        if (!isNotificationEnabled) {
            return Result.success()
        }

        val events: List<ListEventsItem?>? = runBlocking {
            try {
                val dicodingEvent = apiService.getUpcomingEvents()
                dicodingEvent.listEvents
            } catch (e: Exception) {
                Log.e("MyWorker", "Failed to fetch events", e)
                return@runBlocking null
            }
        }

        events?.let {
            if (it.isNotEmpty()) {
                val event = it.first()
                if (event != null) {
                    event.beginTime?.let { it1 ->
                        event.name?.let { it2 ->
                            event.id?.let { it3 ->
                                sendNotification(it2, it1, it3)
                            }
                        }
                    }
                }
            }
        }

        return Result.success()
    }

    private fun sendNotification(eventName: String, eventTime: String, eventId: Int) {

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("eventId", eventId)
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Event Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Set your notification icon
            .setContentTitle("Upcoming Event: $eventName")
            .setContentText("Time: $eventTime")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}