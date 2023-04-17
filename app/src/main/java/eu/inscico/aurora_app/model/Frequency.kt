package eu.inscico.aurora_app.model

import android.content.Context
import eu.inscico.aurora_app.R

enum class Frequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARlY;

    companion object {
        fun Frequency.toDisplayNameRes(): Int {
            return when(this){
                DAILY -> R.string.settings_notifications_frequency_daily_title
                WEEKLY -> R.string.settings_notifications_frequency_weekly_title
                MONTHLY -> R.string.settings_notifications_frequency_monthly_title
                YEARlY -> R.string.settings_notifications_frequency_yearly_title
            }
        }

        fun Frequency.toDisplayName(context: Context): String {
            return context.getString(this.toDisplayNameRes())
        }
    }
}
