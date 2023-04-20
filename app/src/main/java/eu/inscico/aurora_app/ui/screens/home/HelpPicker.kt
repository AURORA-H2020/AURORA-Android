package eu.inscico.aurora_app.ui.screens.home


/*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*
@Composable
fun TimePicker(
    selectedTime: MutableState<Date>,
    modifier: Modifier = Modifier,
) {
    var isTimePickerOpen by remember { mutableStateOf(false) }
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
    val formattedTime = remember(selectedTime.value) {
        timeFormat.format(selectedTime.value)
    }
    Column(modifier = modifier) {
        Text(
            text = formattedTime,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        if (isTimePickerOpen) {
            TimePickerDialog(
                onDismissRequest = { isTimePickerOpen = false },
                selectedTime = selectedTime,
            )
        }
        Button(
            onClick = { isTimePickerOpen = true },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Select Time")
        }
    }
}
@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    selectedTime: MutableState<Date>,
) {
    val time = remember(selectedTime.value) {
        Calendar.getInstance().apply {
            time = selectedTime.value
        }
    }
    val hour = remember(time.get(Calendar.HOUR_OF_DAY)) { mutableStateOf(time.get(Calendar.HOUR_OF_DAY)) }
    val minute = remember(time.get(Calendar.MINUTE)) { mutableStateOf(time.get(Calendar.MINUTE)) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Select Time") },
        buttons = {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(text = "Cancel")
                }
                TextButton(
                    onClick = {
                        time.set(Calendar.HOUR_OF_DAY, hour.value)
                        time.set(Calendar.MINUTE, minute.value)
                        selectedTime.value = time.time
                        onDismissRequest()
                    }
                ) {
                    Text(text = "OK")
                }
            }
        },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TimePickerField(
                    value = hour.value,
                    onValueChange = { hour.value = it },
                    label = "Hour",
                    range = 0..23
                )
                TimePickerField(
                    value = minute.value,
                    onValueChange = { minute.value = it },
                    label = "Minute",
                    range = 0..59
                )
            }
        }
    )
}
@Composable
fun TimePickerField(
    value: Int,
    onValueChange: (Int) -> Unit,
    label: String,
    range: IntRange,
) {

    val input = remember {
        mutableStateOf(value.toString())
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label)
        OutlinedTextField(
            value = input.value,
            onValueChange = {
                input.value = it
            },
            singleLine = true,
            modifier = Modifier.width(80.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = MaterialTheme.typography.h5,
            textAlign = TextAlign.Center,
        )
    }
}

 */