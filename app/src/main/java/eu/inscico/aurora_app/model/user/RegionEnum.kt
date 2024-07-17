package eu.inscico.aurora_app.model.user

import android.content.Context
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptions.ElectricitySource

enum class RegionEnum {
    SYSTEM,
    GERMANY,
    PORTUGAL,
    SLOVENIA,
    DENMARK,
    UNITED_KINGDOM,
    SPAIN,
    USA;

    companion object {
        fun parseStringToRegion(regionString: String?): RegionEnum {
            return when (regionString) {
                "system" -> SYSTEM
                "germany" -> GERMANY
                "portugal" -> PORTUGAL
                "slovenia" -> SLOVENIA
                "denmark" -> DENMARK
                "united_kingdom" -> UNITED_KINGDOM
                "usa" -> USA
                "spain" -> SPAIN
                else -> SYSTEM
            }
        }

        fun parseRegionToString(region: RegionEnum): String {
            return when (region) {
                SYSTEM -> "system"
                GERMANY -> "germany"
                PORTUGAL -> "portugal"
                SLOVENIA -> "slovenia"
                DENMARK -> "denmark"
                SPAIN -> "spain"
                UNITED_KINGDOM -> "united_kingdom"
                USA -> "usa"
            }
        }

        fun RegionEnum.getDisplayNameRes(): Int {
            return when (this) {
                SYSTEM -> R.string.settings_profile_select_region_system_title
                GERMANY -> R.string.settings_profile_select_region_germany_title
                PORTUGAL -> R.string.settings_profile_select_region_portugal_title
                SLOVENIA -> R.string.settings_profile_select_region_slovenia_title
                DENMARK -> R.string.settings_profile_select_region_denmark_title
                UNITED_KINGDOM -> R.string.settings_profile_select_region_united_kingdom_title
                SPAIN -> R.string.settings_profile_select_region_spain_title
                USA -> R.string.settings_profile_select_region_usa_title
            }
        }

        fun RegionEnum.getDisplayName(context: Context): String {
            return context.getString(this.getDisplayNameRes())
        }

        fun getRegionsList(): List<RegionEnum> {
            return listOf(
                SYSTEM,
                GERMANY,
                PORTUGAL,
                SLOVENIA,
                DENMARK,
                SPAIN,
                UNITED_KINGDOM,
                USA
            )
        }

    }
}