package eu.inscico.aurora_app.utils

object FormFieldsUtils {

    fun isDecimalInputValid(input: String): Boolean {
        return input.matches(Regex("^(\\d+(?:,\\d{1,2})?)")) || input.matches(Regex("^(\\d+,)")) || input.matches(Regex("^(\\d+(?:.\\d{1,2})?)")) || input.matches(Regex("^(\\d+.)"))
    }

}