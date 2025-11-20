package eu.inscico.aurora_app.model.consumptions

import android.content.Context
import com.squareup.moshi.Json
import eu.inscico.aurora_app.R

enum class TransportationType {

    // Cars & Motorcycles
    @Json(name = "fuelCar") FUEL_CAR,
    @Json(name = "electricCar") ELECTRIC_CAR,
    @Json(name = "hybridCar") HYBRID_CAR,
    @Json(name = "motorcycle") MOTORCYCLE,
    @Json(name = "electricMotorcycle") ELECTRIC_MOTORCYCLE,

    // Busses
    @Json(name = "electricBus") ELECTRIC_BUS,
    @Json(name = "hybridElectricBus") HYBRID_ELECTRIC_BUS,
    @Json(name = "alternativeFuelBus") ALTERNATIVE_FUEL_BUS,
    @Json(name = "dieselBus") DIESEL_BUS,
    @Json(name = "otherBus") OTHER_BUS,

    // Trains & Trams
    @Json(name = "metroTramOrUrbanLightTrain") METRO_TRAM_OR_URBAN_LIGHT_TRAIN,
    @Json(name = "electricPassengerTrain") ELECTRIC_PASSENGER_TRAIN,
    @Json(name = "dieselPassengerTrain") DIESEL_PASSENGER_TRAIN,
    @Json(name = "highSpeedTrain") HIGH_SPEED_TRAIN,

    // Plane
    @Json(name = "plane") PLANE,
    @Json(name = "planeIntraEu") PLANE_INTRA_EU,
    @Json(name = "planeExtraEu") PLANE_EXTRA_EU,

    // Other
    @Json(name = "electricBike") ELECTRIC_BIKE,
    @Json(name = "electricScooter") ELECTRIC_SCOOTER,
    @Json(name = "bike") BIKE,
    @Json(name = "walking") WALKING;

    companion object {

        fun parseStringToTransportationType(string: String?): TransportationType? {
            return when (string) {
                "fuelCar" -> FUEL_CAR
                "electricCar" -> ELECTRIC_CAR
                "hybridCar" -> HYBRID_CAR
                "motorcycle" -> MOTORCYCLE
                "electricMotorcycle" -> ELECTRIC_MOTORCYCLE
                "electricBus" -> ELECTRIC_BUS
                "hybridElectricBus" -> HYBRID_ELECTRIC_BUS
                "alternativeFuelBus" -> ALTERNATIVE_FUEL_BUS
                "dieselBus" -> DIESEL_BUS
                "otherBus" -> OTHER_BUS
                "metroTramOrUrbanLightTrain" -> METRO_TRAM_OR_URBAN_LIGHT_TRAIN
                "electricPassengerTrain" -> ELECTRIC_PASSENGER_TRAIN
                "dieselPassengerTrain" -> DIESEL_PASSENGER_TRAIN
                "highSpeedTrain" -> HIGH_SPEED_TRAIN
                "plane" -> PLANE
                "planeIntraEu" -> PLANE_INTRA_EU
                "planeExtraEu" -> PLANE_EXTRA_EU
                "electricBike" -> ELECTRIC_BIKE
                "electricScooter" -> ELECTRIC_SCOOTER
                "bike" -> BIKE
                "walking" -> WALKING
                else -> null
            }
        }

        fun TransportationType.parseTransportationTypeToString(): String {
            return when (this) {
                FUEL_CAR -> "fuelCar"
                ELECTRIC_CAR -> "electricCar"
                HYBRID_CAR -> "hybridCar"
                MOTORCYCLE -> "motorcycle"
                ELECTRIC_MOTORCYCLE -> "electricMotorcycle"
                ELECTRIC_BUS -> "electricBus"
                HYBRID_ELECTRIC_BUS -> "hybridElectricBus"
                ALTERNATIVE_FUEL_BUS -> "alternativeFuelBus"
                DIESEL_BUS -> "dieselBus"
                OTHER_BUS -> "otherBus"
                METRO_TRAM_OR_URBAN_LIGHT_TRAIN -> "metroTramOrUrbanLightTrain"
                ELECTRIC_PASSENGER_TRAIN -> "electricPassengerTrain"
                DIESEL_PASSENGER_TRAIN -> "dieselPassengerTrain"
                HIGH_SPEED_TRAIN -> "highSpeedTrain"
                PLANE -> "plane"
                PLANE_INTRA_EU -> "planeIntraEu"
                PLANE_EXTRA_EU -> "planeExtraEu"
                ELECTRIC_BIKE -> "electricBike"
                ELECTRIC_SCOOTER -> "electricScooter"
                BIKE -> "bike"
                WALKING -> "walking"
            }
        }

        fun TransportationType.getDisplayNameRes(): Int {
            return when (this) {
                FUEL_CAR -> R.string.home_add_consumption_transportation_type_item_fuel_car_title
                ELECTRIC_CAR -> R.string.home_add_consumption_transportation_type_item_electric_car_title
                HYBRID_CAR -> R.string.home_add_consumption_transportation_type_item_hybrid_car_title
                MOTORCYCLE -> R.string.home_add_consumption_transportation_type_item_motorcycle_title
                ELECTRIC_MOTORCYCLE -> R.string.home_add_consumption_transportation_type_item_electric_motorcycle_title
                ELECTRIC_BUS -> R.string.home_add_consumption_transportation_type_item_electric_bus_title
                HYBRID_ELECTRIC_BUS -> R.string.home_add_consumption_transportation_type_item_hybrid_bus_title
                ALTERNATIVE_FUEL_BUS -> R.string.home_add_consumption_transportation_type_item_alternative_fuel_bus_title
                DIESEL_BUS -> R.string.home_add_consumption_transportation_type_item_diesel_bus_title
                OTHER_BUS -> R.string.home_add_consumption_transportation_type_item_other_bus_title
                METRO_TRAM_OR_URBAN_LIGHT_TRAIN -> R.string.home_add_consumption_transportation_type_item_metro_tram_or_else_title
                ELECTRIC_PASSENGER_TRAIN -> R.string.home_add_consumption_transportation_type_item_electric_passenger_train_title
                DIESEL_PASSENGER_TRAIN -> R.string.home_add_consumption_transportation_type_item_diesel_passenger_train_title
                HIGH_SPEED_TRAIN -> R.string.home_add_consumption_transportation_type_item_high_speed_train_title
                PLANE -> R.string.home_add_consumption_transportation_type_item_plane_domestic_title
                PLANE_INTRA_EU -> R.string.home_add_consumption_transportation_type_item_plane_intra_eu_title
                PLANE_EXTRA_EU -> R.string.home_add_consumption_transportation_type_item_plane_extra_eu_title
                ELECTRIC_BIKE -> R.string.home_add_consumption_transportation_type_item_electric_bike_title
                ELECTRIC_SCOOTER -> R.string.home_add_consumption_transportation_type_item_electric_scooter_title
                BIKE -> R.string.home_add_consumption_transportation_type_item_bike_title
                WALKING -> R.string.home_add_consumption_transportation_type_item_walking_title
            }
        }

        fun TransportationType.getDisplayName(context: Context): String {
            return context.getString(this.getDisplayNameRes())
        }

    }
}

enum class TransportationTypeSection {
    CARS_AND_MOTORCYCLES,
    BUSSES,
    TRAINS_AND_TRAMS,
    AVIATION,
    OTHER;

    companion object {

        fun TransportationTypeSection.getDisplayName(context: Context): String {
            return when(this){
                CARS_AND_MOTORCYCLES -> context.getString(R.string.home_add_consumption_transportation_type_section_cars_and_bikes_title)
                BUSSES -> context.getString(R.string.home_add_consumption_transportation_type_section_busses_title)
                TRAINS_AND_TRAMS -> context.getString(R.string.home_add_consumption_transportation_type_section_trains_and_trams_title)
                AVIATION -> context.getString(R.string.home_add_consumption_transportation_type_section_aviation_title)
                OTHER -> context.getString(R.string.home_add_consumption_transportation_type_section_other_title)
            }
        }




    }
}

