package eu.inscico.aurora_app.ui.components.recurringConsumptions

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptions.ConsumptionType
import eu.inscico.aurora_app.model.consumptions.ConsumptionType.Companion.getDisplayName
import eu.inscico.aurora_app.model.recurringConsumption.RecurringConsumption
import eu.inscico.aurora_app.model.recurringConsumption.RecurringConsumptionIntervalUnit.Companion.getDisplayName
import eu.inscico.aurora_app.ui.theme.electricityYellow
import eu.inscico.aurora_app.ui.theme.heatingRed
import eu.inscico.aurora_app.ui.theme.mobilityBlue

@Composable
fun RecurringConsumptionListItem(
    recurringConsumption: RecurringConsumption,
    callback: (RecurringConsumption) -> Unit
) {


    val context = LocalContext.current

    val headlineText = recurringConsumption.category.getDisplayName(context)
    val recurringUnitText = recurringConsumption.frequency.unit.getDisplayName(context)
    val iconResource = when(recurringConsumption.category){
        ConsumptionType.ELECTRICITY -> {R.drawable.outline_electric_bolt_24}
        ConsumptionType.HEATING -> {R.drawable.outline_local_fire_department_24}
        ConsumptionType.TRANSPORTATION -> {R.drawable.outline_directions_car_24}
    }

    val iconColor = when(recurringConsumption.category){
        ConsumptionType.ELECTRICITY -> electricityYellow
        ConsumptionType.HEATING -> heatingRed
        ConsumptionType.TRANSPORTATION -> mobilityBlue
    }

    val isEnabledLabel = if(recurringConsumption.isEnabled){
        R.string.home_recurring_consumptions_list_item_enabled_title
    } else {
        R.string.home_recurring_consumptions_list_item_disabled_title
    }

    val backgroundColor = if(recurringConsumption.isEnabled){
        Color(iconColor.value).copy(alpha = 0.2F)
    } else {
        Color(iconColor.value).copy(alpha = 0.1F)
    }
    val foregroundColorFilter = if(recurringConsumption.isEnabled){
        ColorFilter.tint(color = iconColor)
    } else {
        ColorFilter.tint(color = Color(iconColor.value).copy(alpha = 0.4F))
    }

    val headlineTextColor = if(recurringConsumption.isEnabled){
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.outline
    }

    val secondaryTextColor = if(recurringConsumption.isEnabled){
        MaterialTheme.colorScheme.onSecondary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    ListItem(
        modifier = Modifier.clickable {
            callback.invoke(recurringConsumption)
        },
        leadingContent = {

                    Box(
                        modifier = Modifier
                            .drawBehind {
                                drawRoundRect(
                                    color = backgroundColor,
                                    cornerRadius = CornerRadius(16.dp.toPx())
                                )
                            }
                            .size(30.dp)
                    ) {
                        Image(
                            modifier = Modifier
                                .matchParentSize()
                                .padding(6.dp),
                            painter = painterResource(id = iconResource),
                            contentDescription = "",
                            colorFilter = foregroundColorFilter,
                        )
                    }
        },
        headlineContent = {
            Column {
                Text(text = headlineText, color = headlineTextColor)
                Text(
                    text = recurringUnitText,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Start,
                    color = secondaryTextColor
                )
            }
        },
        trailingContent = {
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
                Text(
                    text = stringResource(id = isEnabledLabel),
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Start,
                    color = secondaryTextColor
                )
            }
        }
    )

}