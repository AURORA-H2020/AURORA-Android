package eu.inscico.aurora_app.model.user

import android.content.Context
import com.squareup.moshi.Json
import eu.inscico.aurora_app.R

enum class HouseholdProfileEnum {
    @Json(name = "retiredIndividuals") RETIRED_INDIVIDUALS,
    @Json(name = "homeBasedWorkersOrStudents") HOME_BASED_WORKERS_STUDENTS,
    @Json(name = "homemakers") HOMEMAKERS,
    @Json(name = "workersOrStudentsOutsideTheHome") WORKERS_STUDENTS_OUTSIDE_THE_HOME;

    companion object {

        fun getDisplayRes(value: HouseholdProfileEnum?): Int {
            return when (value) {
                RETIRED_INDIVIDUALS -> R.string.create_profile_household_profile_retired_individuals
                HOME_BASED_WORKERS_STUDENTS -> R.string.create_profile_household_profile_home_based_workers_students
                HOMEMAKERS -> R.string.create_profile_household_profile_homemakers
                WORKERS_STUDENTS_OUTSIDE_THE_HOME -> R.string.create_profile_household_profile_workers_students_outside_home
                null -> R.string.create_profile_drop_down_prefer_not_to_say
            }
        }

        fun getHouseholdProfileDisplayResList(): List<Int> {
            return listOf(
                getDisplayRes(RETIRED_INDIVIDUALS),
                getDisplayRes(HOME_BASED_WORKERS_STUDENTS),
                getDisplayRes(HOMEMAKERS),
                getDisplayRes(WORKERS_STUDENTS_OUTSIDE_THE_HOME),
                R.string.create_profile_drop_down_prefer_not_to_say
            )
        }

        fun getHouseholdProfileDisplayList(context: Context): List<String> {
            return listOf(
                context.getString(getDisplayRes(RETIRED_INDIVIDUALS)),
                context.getString(getDisplayRes(HOME_BASED_WORKERS_STUDENTS)),
                context.getString(getDisplayRes(HOMEMAKERS)),
                context.getString(getDisplayRes(WORKERS_STUDENTS_OUTSIDE_THE_HOME)),
                context.getString(R.string.create_profile_drop_down_prefer_not_to_say)
            )
        }


        fun HouseholdProfileEnum?.toHouseholdProfileString(context: Context): String {
            return when (this) {
                RETIRED_INDIVIDUALS -> context.getString(R.string.create_profile_household_profile_retired_individuals)
                HOME_BASED_WORKERS_STUDENTS -> context.getString(R.string.create_profile_household_profile_home_based_workers_students)
                HOMEMAKERS -> context.getString(R.string.create_profile_household_profile_homemakers)
                WORKERS_STUDENTS_OUTSIDE_THE_HOME -> context.getString(R.string.create_profile_household_profile_workers_students_outside_home)
                null -> context.getString(R.string.create_profile_drop_down_prefer_not_to_say)
            }
        }

        fun parseStringToHouseholdProfile(value: String?): HouseholdProfileEnum? {
            return when (value) {
                "retiredIndividuals" -> RETIRED_INDIVIDUALS
                "homeBasedWorkersOrStudents" -> HOME_BASED_WORKERS_STUDENTS
                "homemakers" -> HOMEMAKERS
                "workersOrStudentsOutsideTheHome" -> WORKERS_STUDENTS_OUTSIDE_THE_HOME
                else -> null
            }
        }

        fun parseHouseholdProfileToString(value: HouseholdProfileEnum?): String? {
            return when (value) {
                RETIRED_INDIVIDUALS -> "retiredIndividuals"
                HOME_BASED_WORKERS_STUDENTS -> "homeBasedWorkersOrStudents"
                HOMEMAKERS -> "homemakers"
                WORKERS_STUDENTS_OUTSIDE_THE_HOME -> "workersOrStudentsOutsideTheHome"
                null -> null
            }
        }
    }
}