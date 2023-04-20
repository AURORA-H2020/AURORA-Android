package eu.inscico.aurora_app.services.notification

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import eu.inscico.aurora_app.core.MainActivity

class NotificationPermissionHandler(
    private val _activity: MainActivity
) {

    private var _requestPermissionLauncher: ActivityResultLauncher<String>? = null

    init {
        // should only be called once at MainActivity lifecycle start
        registerPermissionLauncher()
    }

    private fun registerPermissionLauncher() {

        _requestPermissionLauncher = _activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->

            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your app.
                // do nothing
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their decision.

                // do nothing
            }
        }
    }

    fun checkAndHandleNotificationPermission() {

        // new notification permission (Android 13+)
        val areNotificationsEnabled =
            NotificationManagerCompat.from(_activity).areNotificationsEnabled()

        if (Build.VERSION.SDK_INT < 33 || areNotificationsEnabled) {

            if (Build.VERSION.SDK_INT < 33) {
                // THIS should be done in "_requestPermissionLauncher", but it's not called below API 33
                // Permission is granted. Continue the action or workflow in your app.
            }
        } else {
            when {
                ContextCompat.checkSelfPermission(
                    _activity, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // You can use the API that requires the permission.
                }
                _activity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {

                    // if "shouldShowRequestPermissionRationale" is unclear see:
                    // https://stackoverflow.com/a/41312851/4962151

                    // TODO: change to dialog! it often will launch immediately after app start if noti. disallowed!
                    //  or just leave it out?
                    /*val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    val uri: Uri = Uri.fromParts("package", _activity.packageName, null)
                    intent.data = uri
                    _activity.startActivity(intent)*/

                    _requestPermissionLauncher?.launch(
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                }
                else -> {
                    // The registered ActivityResultCallback gets the result of this request
                    _requestPermissionLauncher?.launch(
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                }
            }
        }
    }
}
