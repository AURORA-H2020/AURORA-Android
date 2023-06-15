package eu.inscico.aurora_app.ui.components.recurringConsumptions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.recurringConsumption.RecurringConsumptionFrequency
import eu.inscico.aurora_app.model.recurringConsumption.RecurringConsumptionIntervalUnit
import eu.inscico.aurora_app.model.recurringConsumption.RecurringConsumptionIntervalUnit.Companion.getDisplayName
import eu.inscico.aurora_app.model.recurringConsumption.RecurringConsumptionIntervalWeekday
import eu.inscico.aurora_app.model.recurringConsumption.RecurringConsumptionIntervalWeekday.Companion.getDisplayName
import eu.inscico.aurora_app.ui.components.forms.MultiSelectEntry
import eu.inscico.aurora_app.ui.components.forms.MultiSelectSpinner
import eu.inscico.aurora_app.ui.components.forms.SpinnerFormEntry
import eu.inscico.aurora_app.ui.components.forms.SpinnerItem

@Composable
fun FrequencySpinner(
    initialValues: RecurringConsumptionFrequency?,
    callback: (RecurringConsumptionFrequency) -> Unit
) {

    val context = LocalContext.current

    val intervalUnit = rememberSaveable {
        mutableStateOf(initialValues?.unit ?: RecurringConsumptionIntervalUnit.DAILY)
    }

    val updatedWeekdays = RecurringConsumptionIntervalWeekday.getWeekdayList().map {
        MultiSelectEntry(name = it.getDisplayName(context), data = it, isSelected = false)
    }
    updatedWeekdays.forEach { weekday ->
        initialValues?.weekdays?.forEach { selectedWeekday ->
            if (selectedWeekday.name == weekday.data.name) {
                weekday.isSelected = true
            }
        }
    }
    val weekdayEntries =
        rememberSaveable { mutableStateOf<List<MultiSelectEntry<*>>?>(updatedWeekdays) }

    val selectedWeekdays = rememberSaveable {
        mutableStateOf(weekdayEntries.value?.filter { it.isSelected })
    }

    val unitMonthlyDay = rememberSaveable {
        mutableStateOf<Int?>(initialValues?.dayOfMonth)
    }

    val allSelectableUnits = RecurringConsumptionIntervalUnit.getIntervalUnitList().map {
        SpinnerItem.Entry(name = it.getDisplayName(context), data = it)
    }

    val selectedUnit = SpinnerItem.Entry(
        name = intervalUnit.value.getDisplayName(context),
        data = intervalUnit.value
    )

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(shape = RoundedCornerShape(16.dp))
    ) {


        SpinnerFormEntry(
            title = stringResource(id = R.string.home_recurring_consumptions_frequency_title),
            selectedEntry = selectedUnit,
            allEntries = allSelectableUnits,
            isRoundedDesign = false,
            callback = { item, _ ->
                intervalUnit.value = item.data as RecurringConsumptionIntervalUnit
            }
        )

        when (intervalUnit.value) {
            RecurringConsumptionIntervalUnit.DAILY -> {
                val frequency = RecurringConsumptionFrequency(
                    unit = RecurringConsumptionIntervalUnit.DAILY,
                    weekdays = null,
                    dayOfMonth = null
                )
                callback.invoke(frequency)
            }
            RecurringConsumptionIntervalUnit.WEEKLY -> {

                Divider()

                MultiSelectSpinner(
                    title = stringResource(id = R.string.home_recurring_consumptions_frequency_weekly_weekday_title),
                    entries = weekdayEntries.value,
                    callback = { items ->
                        weekdayEntries.value = items as List<MultiSelectEntry<*>>
                        selectedWeekdays.value = getSelectedEntries(items)

                        val frequency = RecurringConsumptionFrequency(
                            unit = RecurringConsumptionIntervalUnit.WEEKLY,
                            weekdays = selectedWeekdays.value?.map {
                                it.data as RecurringConsumptionIntervalWeekday
                            },
                            dayOfMonth = null
                        )
                        callback.invoke(frequency)
                    }
                )
            }
            RecurringConsumptionIntervalUnit.MONTHLY -> {

                Divider()

                val allSelectableDays = (1..31).map {
                    SpinnerItem.Entry(name = "$it", data = it)
                }

                val selectedDay = if (unitMonthlyDay.value != null) {
                    SpinnerItem.Entry(
                        name = unitMonthlyDay.value.toString(),
                        data = unitMonthlyDay.value
                    )
                } else {
                    null
                }

                SpinnerFormEntry(
                    title = stringResource(id = R.string.settings_notifications_day_title),
                    selectedEntry = selectedDay,
                    allEntries = allSelectableDays,
                    isRoundedDesign = false,
                    callback = { item, _ ->
                        val dayOfMonth = item.data as Int
                        val frequency = RecurringConsumptionFrequency(
                            unit = RecurringConsumptionIntervalUnit.MONTHLY,
                            weekdays = null,
                            dayOfMonth = dayOfMonth
                        )
                        unitMonthlyDay.value = dayOfMonth
                        callback.invoke(frequency)
                    }
                )
            }
        }
    }
}


fun getSelectedEntries(entries: List<MultiSelectEntry<*>>?): List<MultiSelectEntry<*>>? {
    return entries?.filter {
        it.isSelected
    }
}
