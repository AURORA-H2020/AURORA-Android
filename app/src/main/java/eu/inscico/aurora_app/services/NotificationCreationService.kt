package eu.inscico.aurora_app.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.core.MainActivity
import eu.inscico.aurora_app.model.push.PushNotificationType
import org.json.JSONObject
import java.util.*


class NotificationCreationService(val context: Context) {

    companion object {
        const val PUSH_TARGET = "PUSH_TARGET"
        const val PUSH_VALUE = "PUSH_VALUE"
    }

    // -- API ----------------------------------------------------------------------------------------------------------


    fun showNotification(
        title: String = "",
        message: String = "",
        pushNotificationType: PushNotificationType,
        payload: JSONObject? = null,
    ) {
        // Generate random notification UUID to show multiple notifications, even if it is the same
        val randomNotificationUUID = UUID.randomUUID().hashCode()

        // Create notificationIntent
        val notificationIntent = Intent(context, MainActivity::class.java).apply {

            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(PUSH_TARGET, pushNotificationType.key)
            if (payload != null) {
                putExtra(PUSH_VALUE, payload.toString())
            }
        }

        val flags = if (Build.VERSION.SDK_INT >= 31) {
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_CANCEL_CURRENT
        }

        // Create pendingIntent
        val pendingIntent = PendingIntent.getActivity(
            context,
            randomNotificationUUID,
            notificationIntent,
            flags
        )

        // Get NotificationManager
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        createNotificationChannel( notificationManager, pushNotificationType)

        // Create Notification
        val notification = createNotification( title, message, pushNotificationType, pendingIntent)

        // Show Notification
        notificationManager.notify(randomNotificationUUID, notification)
    }

    fun removeNotification(id: Int) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(id)
    }


    // -- Helper -------------------------------------------------------------------------------------------------------

    private fun createNotification(
        title: String,
        message: String,
        pushNotificationType: PushNotificationType,
        pendingIntent: PendingIntent
    ): Notification {

        val pushNotificationConfig = PushNotificationType.getPushNotificationConfig(pushNotificationType)

        return NotificationCompat.Builder(context, pushNotificationConfig.channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.aurora_logo)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createNotificationChannel(
        notificationManager: NotificationManager,
        pushNotificationType: PushNotificationType
    ) {

        val pushNotificationConfig = PushNotificationType.getPushNotificationConfig(pushNotificationType)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                pushNotificationConfig.channelId,
                pushNotificationConfig.channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                setShowBadge(true)
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

}