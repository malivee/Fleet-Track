package com.test.fleettrack.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.preference.PreferenceManager
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.test.fleettrack.R

class ServiceHandler(private val context: Context, private val params: WorkerParameters) :
    Worker(context, params) {

        private val id = inputData.getLong(STATUS_ID, 0)
        private val isDoorOpened = inputData.getBoolean(STATUS_DOOR, false)
        private val isEngineStarted = inputData.getBoolean(STATUS_ENGINE, false)
    override fun doWork(): Result {
        val prefManager =
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val shouldNotify =
            prefManager.getBoolean("NOTIFICATION_KEY", false)
        if (shouldNotify) {
            if (!isDoorOpened || !isEngineStarted) {
                showNotification()
            }
        }
        return Result.success()
    }

    private fun showNotification() {
        val notifyTitle ="Warning"

        val issues = mutableListOf<String>()

        if (!isDoorOpened) {
            issues.add("Door is opened")
        }
        if (!isEngineStarted) {
            issues.add("Engine is off")
        }


        val notifyBody = "There is something wrong with the vehicle: ${issues.joinToString(", ")}"



        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
            .setContentTitle(notifyTitle)
            .setContentText(notifyBody)
            .setSmallIcon(R.drawable.si_warning_fill)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH

            )

            notificationBuilder.setChannelId(NOTIFICATION_CHANNEL)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notification = notificationBuilder.build()
        notificationManager.notify(NOTIFICATION_CHANNEL, id.toInt(), notification)


    }

    companion object {
        private const val NOTIFICATION_CHANNEL = "notify_channel"
        private const val CHANNEL_NAME = "alert_vehicle"
        const val STATUS_ID = "status_id"
        const val STATUS_DOOR = "status_door"
        const val STATUS_ENGINE = "status_engine"
    }


}