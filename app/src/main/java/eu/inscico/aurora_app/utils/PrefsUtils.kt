package eu.inscico.aurora_app.utils

import android.content.Context

object PrefsUtils {

    const val PREFS_NAME = "DEFAULT_APP_PREFS"

    fun save(
        context: Context,
        tag: String,
        data: Any,
        prefsName: String = PREFS_NAME,
        mode: Int = Context.MODE_PRIVATE
    ): Boolean {
        // Get prefs
        val prefs = context.getSharedPreferences(prefsName, mode)
        val editor = prefs.edit()

        // Write data
        when (data) {
            is Int -> editor.putInt(tag, data)
            is Long -> editor.putLong(tag, data)
            is Float -> editor.putFloat(tag, data)
            is String -> editor.putString(tag, data)
            is Boolean -> editor.putBoolean(tag, data)
            else -> return false
        }
        editor.apply()

        return true
    }

    inline fun <reified T> get(
        context: Context,
        tag: String,
        defaultValue: T,
        prefsName: String = PREFS_NAME,
        mode: Int = Context.MODE_PRIVATE
    ): T {
        // Get prefs
        val prefs = context.getSharedPreferences(prefsName, mode)

        // Get data
        return when (T::class) {
            Int::class -> prefs.getInt(tag, defaultValue as Int) as T
            Long::class -> prefs.getLong(tag, defaultValue as Long) as T
            Float::class -> prefs.getFloat(tag, defaultValue as Float) as T
            String::class -> prefs.getString(tag, defaultValue as String) as T
            Boolean::class -> prefs.getBoolean(tag, defaultValue as Boolean) as T
            else -> defaultValue
        }
    }

    fun contains(
        context: Context,
        tag: String,
        prefsName: String = PREFS_NAME,
        mode: Int = Context.MODE_PRIVATE
    ): Boolean {
        // Get prefs
        val prefs = context.getSharedPreferences(prefsName, mode)

        // Check contains
        return prefs.contains(tag)
    }

    fun remove(
        context: Context,
        tag: String,
        prefsName: String = PREFS_NAME,
        mode: Int = Context.MODE_PRIVATE
    ) {
        // Get prefs
        val prefs = context.getSharedPreferences(prefsName, mode)

        // Remove data
        val editor = prefs.edit()
        editor.remove(tag).apply()
    }

    fun clearAllPrefs(
        context: Context,
        prefsName: String = PREFS_NAME,
        mode: Int = Context.MODE_PRIVATE
    ) {
        // Get prefs
        val prefs = context.getSharedPreferences(prefsName, mode)

        // Clear prefs
        val editor = prefs.edit()
        editor.clear().apply()
    }

}