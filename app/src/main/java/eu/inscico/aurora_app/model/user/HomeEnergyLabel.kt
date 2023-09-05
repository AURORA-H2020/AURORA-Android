package eu.inscico.aurora_app.model.user

import android.content.Context
import com.squareup.moshi.Json
import eu.inscico.aurora_app.R

enum class HomeEnergyLabel {
    @Json(name = "A+") A_PLUS,
    @Json(name = "C") A,
    @Json(name = "B") B,
    @Json(name = "C") C,
    @Json(name = "D") D,
    @Json(name = "E") E,
    @Json(name = "F") F,
    @Json(name = "G") G,
    @Json(name = "unsure") UNSURE;

    companion object {

        fun getDisplayRes(label: HomeEnergyLabel?): Int {
            return when (label) {
                A_PLUS -> R.string.create_profile_home_energy_level_A_plus
                A -> R.string.create_profile_home_energy_level_A
                B -> R.string.create_profile_home_energy_level_B
                C -> R.string.create_profile_home_energy_level_C
                D -> R.string.create_profile_home_energy_level_D
                E -> R.string.create_profile_home_energy_level_E
                F -> R.string.create_profile_home_energy_level_F
                G -> R.string.create_profile_home_energy_level_G
                UNSURE -> R.string.create_profile_home_energy_level_unsure
                null -> R.string.create_profile_drop_down_prefer_not_to_say
            }
        }

        fun getHomeLabelDisplayResList(): List<Int> {
            return listOf(
                getDisplayRes(A_PLUS),
                getDisplayRes(A),
                getDisplayRes(B),
                getDisplayRes(C),
                getDisplayRes(D),
                getDisplayRes(E),
                getDisplayRes(F),
                getDisplayRes(G),
                getDisplayRes(UNSURE),
                R.string.create_profile_drop_down_prefer_not_to_say
            )
        }

        fun getHomeLabelDisplayList(context: Context): List<String> {
            return listOf(
                context.getString(getDisplayRes(A_PLUS)),
                context.getString(getDisplayRes(A)),
                context.getString(getDisplayRes(B)),
                context.getString(getDisplayRes(C)),
                context.getString(getDisplayRes(D)),
                context.getString(getDisplayRes(E)),
                context.getString(getDisplayRes(F)),
                context.getString(getDisplayRes(G)),
                context.getString(getDisplayRes(UNSURE)),
                context.getString(R.string.create_profile_drop_down_prefer_not_to_say)
            )
        }


        fun HomeEnergyLabel?.toHomeLabelString(context: Context): String {
            return when (this) {
                A_PLUS -> context.getString(R.string.create_profile_home_energy_level_A_plus)
                A -> context.getString(R.string.create_profile_home_energy_level_A)
                B -> context.getString(R.string.create_profile_home_energy_level_B)
                C -> context.getString(R.string.create_profile_home_energy_level_C)
                D -> context.getString(R.string.create_profile_home_energy_level_D)
                E -> context.getString(R.string.create_profile_home_energy_level_E)
                F -> context.getString(R.string.create_profile_home_energy_level_F)
                G -> context.getString(R.string.create_profile_home_energy_level_G)
                UNSURE -> context.getString(R.string.create_profile_home_energy_level_unsure)
                null -> context.getString(R.string.create_profile_drop_down_prefer_not_to_say)
            }
        }

        fun parseStringToHomeLabel(label: String?): HomeEnergyLabel? {
            return when (label) {
                "A+" -> A_PLUS
                "A" -> A
                "B" -> B
                "C" -> C
                "D" -> D
                "E" -> E
                "F" -> F
                "G" -> G
                "unsure" -> UNSURE
                else -> null
            }
        }

        fun parseHomeLabelToString(label: HomeEnergyLabel?): String? {
            return when (label) {
                A_PLUS -> "A+"
                A -> "A"
                B -> "B"
                C -> "C"
                D -> "D"
                E -> "E"
                F -> "F"
                G -> "G"
                UNSURE -> "unsure"
                null -> null
            }
        }
    }
}