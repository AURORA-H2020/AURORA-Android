package eu.inscico.aurora_app.ui.components.consumptions

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptions.ConsumptionType
import eu.inscico.aurora_app.model.consumptions.ConsumptionType.Companion.getDisplayNameRes
import eu.inscico.aurora_app.ui.theme.electricityYellow
import eu.inscico.aurora_app.ui.theme.heatingRed
import eu.inscico.aurora_app.ui.theme.mobilityBlue

@Composable
fun AddConsumptionButton(
    consumptionType: ConsumptionType,
    callback: (ConsumptionType) -> Unit
){

    val iconRes = when(consumptionType){
        ConsumptionType.ELECTRICITY -> R.drawable.outline_electric_bolt_24
        ConsumptionType.HEATING -> R.drawable.outline_local_fire_department_24
        ConsumptionType.TRANSPORTATION -> R.drawable.outline_directions_car_24
    }

    val consumptionColor = when(consumptionType){
        ConsumptionType.ELECTRICITY -> electricityYellow
        ConsumptionType.HEATING -> heatingRed
        ConsumptionType.TRANSPORTATION -> mobilityBlue
    }

        Button(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = consumptionColor.copy(alpha = 0.2F)),
        onClick = {
            callback.invoke(consumptionType)
        }) {

        Row(
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Image(
                modifier = Modifier.padding(6.dp),
                painter = painterResource(id = iconRes),
                contentDescription = "",
                colorFilter = ColorFilter.tint(color = consumptionColor),
            )
            Text(
                modifier = Modifier.padding(vertical = 6.dp),
                text = stringResource(id = consumptionType.getDisplayNameRes()),
                style = MaterialTheme.typography.labelLarge,
                color = consumptionColor
            )
        }

    }
}