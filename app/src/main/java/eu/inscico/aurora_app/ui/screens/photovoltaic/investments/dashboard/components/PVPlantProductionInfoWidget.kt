package eu.inscico.aurora_app.ui.screens.photovoltaic.investments.dashboard.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.City
import eu.inscico.aurora_app.model.country.Country
import eu.inscico.aurora_app.model.pvPlant.PVPlant
import eu.inscico.aurora_app.services.shared.UnitService
import eu.inscico.aurora_app.ui.components.ActionEntry
import eu.inscico.aurora_app.ui.components.DecoratedHeadline
import eu.inscico.aurora_app.ui.theme.electricityYellow
import eu.inscico.aurora_app.utils.CalendarUtils
import org.koin.androidx.compose.get

@Composable
fun PVPlantProductionInfoWidget(
    pvPlant: PVPlant,
    userCity: City?,
    userCountry: Country?,
    unitService: UnitService = get()
) {

    val config = LocalConfiguration.current

    DecoratedHeadline(
        headline = "Solar Panel Information",
        leadingIconRes = R.drawable.outline_brightness_empty_24,
        leadingIconColor = electricityYellow
    )

    Spacer(Modifier.height(16.dp))

    Column(
        Modifier
            .clip(shape = RoundedCornerShape(16.dp))
            .fillMaxSize()
    ) {

        ActionEntry(
            title = "${pvPlant.capacity} kW",
            iconRes = R.drawable.outline_battery_charging_full_24,
            isNavigation = false,
            titleColor = MaterialTheme.colorScheme.onSurface,
        )

        val locationNamesList = mutableListOf<String>()

        if (userCity != null) {
            locationNamesList.add(userCity.name)
        }
        if(userCountry?.displayName != null){
            locationNamesList.add(userCountry.displayName!!)
        }
        val locationString = if(locationNamesList.size > 1){
            locationNamesList.joinToString(", ")
        } else if(locationNamesList.size == 1){
            locationNamesList.first()
        } else {
            ""
        }
        if(locationString.isNotEmpty()) {
            Divider()

            ActionEntry(
                title = locationString,
                iconRes = R.drawable.outline_my_location_24,
                isNavigation = false
            )
        }

        pvPlant.manufacturer?.let {
            Divider()

            ActionEntry(
                title = it,
                iconRes = R.drawable.outline_business_24,
                isNavigation = false
            )
        }

        pvPlant.technology?.let {
            Divider()

            ActionEntry(
                title = it,
                iconRes = R.drawable.outline_monitor_heart_24,
                isNavigation = false
            )
        }

        pvPlant.installationDate?.let {
            Divider()

            val installationDateString = CalendarUtils.toDateString(
                it,
                unitService.getDateFormat(config)
            )
            ActionEntry(
                title = installationDateString,
                iconRes = R.drawable.outline_date_range_24,
                isNavigation = false
            )
        }
    }
}