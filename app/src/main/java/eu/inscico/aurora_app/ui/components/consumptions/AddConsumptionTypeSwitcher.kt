package eu.inscico.aurora_app.ui.components.consumptions

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptions.Consumption
import eu.inscico.aurora_app.model.consumptions.ConsumptionType
import eu.inscico.aurora_app.model.consumptions.ConsumptionType.Companion.getDisplayName
import eu.inscico.aurora_app.ui.theme.electricityYellow
import eu.inscico.aurora_app.ui.theme.heatingRed
import eu.inscico.aurora_app.ui.theme.mobilityBlue

@Composable
fun AddConsumptionTypeSwitcher(
    consumptionType: ConsumptionType,
    callback: (ConsumptionType) -> Unit
) {

    val context = LocalContext.current

    val openPopupMenu = remember {
        mutableStateOf(false)
    }

    val selectedConsumption = remember {
        mutableStateOf(consumptionType)
    }

    val consumptionColor = when (selectedConsumption.value) {
        ConsumptionType.ELECTRICITY -> electricityYellow
        ConsumptionType.HEATING -> heatingRed
        ConsumptionType.TRANSPORTATION -> mobilityBlue
    }

    fun getIconRes(type: ConsumptionType): Int {
        return when (type) {
            ConsumptionType.ELECTRICITY -> R.drawable.outline_electric_bolt_24
            ConsumptionType.HEATING -> R.drawable.outline_local_fire_department_24
            ConsumptionType.TRANSPORTATION -> R.drawable.outline_directions_car_24
        }
    }

    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {


        Button(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = consumptionColor.copy(alpha = 0.2F)),
            shape = RoundedCornerShape(32.dp),
            onClick = {
                openPopupMenu.value = !openPopupMenu.value
            }) {

            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 32.dp)
            ) {
                Image(
                    modifier = Modifier.padding(6.dp),
                    painter = painterResource(id = getIconRes(selectedConsumption.value)),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(color = consumptionColor),
                )
                Text(
                    modifier = Modifier.padding(vertical = 6.dp),
                    text = selectedConsumption.value.getDisplayName(context),
                    style = MaterialTheme.typography.labelLarge,
                    color = consumptionColor
                )
                Image(
                    painterResource(id = R.drawable.outline_arrow_drop_down_24),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(consumptionColor),
                    alignment = Alignment.CenterEnd
                )
            }

            DropdownMenu(
                modifier = Modifier.fillMaxWidth(),
                expanded = openPopupMenu.value,
                onDismissRequest = { openPopupMenu.value = false },
            ) {

                val consumptionSwitchItems = when (selectedConsumption.value) {
                    ConsumptionType.ELECTRICITY -> {
                        listOf(ConsumptionType.TRANSPORTATION, ConsumptionType.HEATING)
                    }
                    ConsumptionType.HEATING -> {
                        listOf(ConsumptionType.TRANSPORTATION, ConsumptionType.ELECTRICITY)
                    }
                    ConsumptionType.TRANSPORTATION -> {
                        listOf(ConsumptionType.ELECTRICITY, ConsumptionType.HEATING)
                    }
                }

                consumptionSwitchItems.forEach {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                selectedConsumption.value = it
                                callback.invoke(it)
                                openPopupMenu.value = false
                            }) {
                        Image(
                            painterResource(id = getIconRes(it)),
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                        )
                        Text(
                            text = it.getDisplayName(context),
                            style = MaterialTheme.typography.bodyMedium
                        )

                    }
                }
            }
        }
    }
}