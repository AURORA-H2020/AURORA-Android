package eu.inscico.aurora_app.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

enum class PendingIntentCategory {
    ACTIVITY,
    BROADCAST,
    SERVICE
}

object IntentUtils {

    fun getCorrectPendingIntent(
        context: Context?,
        id: Int,
        intent: Intent,
        flag: Int,
        category: PendingIntentCategory
    ): PendingIntent {

        val correctFlagsPerAPILevel = if (Build.VERSION.SDK_INT < 31) {
            flag
        } else {
            flag or PendingIntent.FLAG_IMMUTABLE
        }
        return when (category) {
            PendingIntentCategory.ACTIVITY -> {
                PendingIntent.getActivity(context, id, intent, correctFlagsPerAPILevel)
            }
            PendingIntentCategory.BROADCAST -> {
                PendingIntent.getBroadcast(context, id, intent, correctFlagsPerAPILevel)
            }
            PendingIntentCategory.SERVICE -> {
                if (Build.VERSION.SDK_INT >= 26) {
                    PendingIntent.getForegroundService(context, id, intent, correctFlagsPerAPILevel)
                } else {
                    PendingIntent.getService(context, id, intent, correctFlagsPerAPILevel)
                }
            }
        }
    }
}