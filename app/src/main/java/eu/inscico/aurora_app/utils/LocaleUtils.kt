package eu.inscico.aurora_app.utils

import android.content.Context
import android.content.res.Configuration
import java.util.*

object LocaleUtils {


    // Function to update the app's locale
    fun updateLocale(context: Context, locale: Locale) {
        val resources = context.resources
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
}