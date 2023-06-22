package eu.inscico.aurora_app.services.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.NotificationCompat
import com.patrykandpatrick.vico.compose.component.shapeComponent
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.core.MainActivity
import eu.inscico.aurora_app.model.push.PushNotificationType
import eu.inscico.aurora_app.ui.components.dialogs.ChangeBehaviourPopupDialog
import org.json.JSONObject
import java.util.*


class NotificationCreationService(val context: Context) {

    companion object {
        const val PUSH_TARGET = "PUSH_TARGET"
        const val PUSH_VALUE = "PUSH_VALUE"
    }

    // -- show notification ----------------------------------------------------------------------------------------------------------

    var _showChangeBehaviorDialog = mutableStateOf(false)


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
        createNotificationChannel(notificationManager, pushNotificationType)

        // Create Notification
        val notification = createNotification(title, message, pushNotificationType, pendingIntent)

        // Show Notification
        notificationManager.notify(randomNotificationUUID, notification)
    }

    fun removeNotification(id: Int) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(id)
    }

    fun showChangeBehaviorDialog(
        onPositiveButtonCallback: () -> Unit,
        onNeutralButtonCallback: () -> Unit
    ) {

        positiveButtonCallback = onPositiveButtonCallback
        neutralButtonCallback = onNeutralButtonCallback

        _showChangeBehaviorDialog.value = true
    }


    // -- create notification -------------------------------------------------------------------------------------------------------

    private fun createNotification(
        title: String,
        message: String,
        pushNotificationType: PushNotificationType,
        pendingIntent: PendingIntent
    ): Notification {

        val pushNotificationConfig =
            PushNotificationType.getPushNotificationConfig(pushNotificationType)

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

        val pushNotificationConfig =
            PushNotificationType.getPushNotificationConfig(pushNotificationType)

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

    var positiveButtonCallback: () -> Unit = {}
    var neutralButtonCallback: () -> Unit = {}
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun getChangeBehaviorPopupDialog() {

        Dialog(onDismissRequest = { _showChangeBehaviorDialog.value = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(shape = RoundedCornerShape(16.dp)),
                ) {
                    Column(modifier = Modifier.padding(4.dp)) {

                        Text(
                            text = stringResource(id = R.string.recurring_consumptions_changed_behaviour_popup_text),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Row(horizontalArrangement = Arrangement.Start) {
                                TextButton(onClick = {
                                    _showChangeBehaviorDialog.value = false
                                    neutralButtonCallback.invoke()
                                }) {
                                    Text(text = stringResource(id = R.string.dont_ask_again), style = MaterialTheme.typography.labelLarge, modifier = Modifier)
                                }

                            }


                            Row(horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = {
                                    _showChangeBehaviorDialog.value = false
                                }) {
                                    Text(stringResource(id = R.string.no), style = MaterialTheme.typography.labelLarge, modifier = Modifier)
                                }

                                TextButton(onClick = {
                                    positiveButtonCallback.invoke()
                                    _showChangeBehaviorDialog.value = false
                                }
                                ) {
                                    Text(text = stringResource(id = R.string.yes), style = MaterialTheme.typography.labelLarge, modifier = Modifier)
                                }


                            }
                        }
                    }
                }

            }

        }
    }

}