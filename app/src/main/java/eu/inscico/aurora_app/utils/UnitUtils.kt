package eu.inscico.aurora_app.utils

import android.content.res.Configuration
import android.icu.util.LocaleData
import android.icu.util.ULocale
import java.util.*

object UnitUtils {

    fun getSystemDistanceUnit(localeString: String): String {
        return ""
    }

    fun getSystemWeightUnit(): String {
        return ""
    }

    fun getSystemCurrencyUnit(config: Configuration): String {
        val currentLocale = config.locales[0]
        val currencySymbol = Currency.getInstance(currentLocale).symbol
        return currencySymbol
    }

    fun getDistanceUnitByLocale(): String {
        return ""
    }

    fun getWeightUnitByLocale(): String {
        return ""
    }

    fun getCurrencyUnitByLocale(locale: String): String {
        val currency: Currency = Currency.getInstance(locale)
        return currency.symbol
    }

}