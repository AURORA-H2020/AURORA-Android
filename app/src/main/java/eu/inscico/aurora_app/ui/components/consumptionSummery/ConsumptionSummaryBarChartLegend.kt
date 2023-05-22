package eu.inscico.aurora_app.ui.components.consumptionSummery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.ui.theme.electricityYellow
import eu.inscico.aurora_app.ui.theme.heatingRed
import eu.inscico.aurora_app.ui.theme.mobilityBlue

@Composable
fun ConsumptionSummaryBarChartLegend(){

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(50))
                .background(electricityYellow)
        )

        Text(modifier = Modifier.padding(start = 4.dp, end = 8.dp), text = stringResource(id = R.string.home_consumptions_type_electricity_title), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondary)


        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(50))
                .background(heatingRed)
        )

        Text(modifier = Modifier.padding(start = 4.dp, end = 8.dp), text = stringResource(id = R.string.home_consumptions_type_heating_title), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondary)

        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(50))
                .background(mobilityBlue)
        )

        Text(modifier = Modifier.padding(start = 4.dp, end = 8.dp), text = stringResource(id = R.string.home_consumptions_type_transportation_title), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondary)
    }
}