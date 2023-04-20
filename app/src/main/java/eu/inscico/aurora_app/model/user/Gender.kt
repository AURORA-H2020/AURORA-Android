package eu.inscico.aurora_app.model.user

import android.content.Context
import com.squareup.moshi.Json
import eu.inscico.aurora_app.R

enum class Gender {

    @Json(name = "male") MALE,
    @Json(name = "female") FEMALE,
    @Json(name = "nonBinary") NON_BINARY,
    @Json(name = "other") OTHER;

    companion object {

        fun getDisplayRes(gender: Gender): Int {
            return when (gender) {
                MALE -> R.string.profile_gender_male
                FEMALE -> R.string.profile_gender_female
                NON_BINARY -> R.string.profile_gender_non_binary
                OTHER -> R.string.create_profile_drop_down_prefer_not_to_say
            }
        }

        fun getGenderDisplayResList(): List<Int> {
            return listOf(
                getDisplayRes(MALE),
                getDisplayRes(FEMALE),
                getDisplayRes(NON_BINARY),
                getDisplayRes(OTHER)
            )
        }

        fun getGenderDisplayList(context: Context): List<String> {
            return listOf(
                context.getString(getDisplayRes(MALE)),
                context.getString(getDisplayRes(FEMALE)),
                context.getString(getDisplayRes(NON_BINARY)),
                context.getString(getDisplayRes(OTHER))
            )
        }


        fun Gender.toGenderString(context: Context): String {
            return when (this) {
                MALE -> context.getString(R.string.profile_gender_male)
                FEMALE -> context.getString(R.string.profile_gender_female)
                NON_BINARY -> context.getString(R.string.profile_gender_non_binary)
                OTHER -> context.getString(R.string.create_profile_drop_down_prefer_not_to_say)
            }
        }

        fun parseStringToGender(genderString: String?): Gender {
            return when (genderString) {
                "male" -> MALE
                "female" -> FEMALE
                "nonBinary" -> NON_BINARY
                else -> OTHER
            }
        }

        fun parseGenderToString(gender: Gender?): String {
            return when (gender) {
                MALE -> "male"
                FEMALE -> "female"
                NON_BINARY -> "nonBinary"
                else -> "other"
            }
        }
    }

}