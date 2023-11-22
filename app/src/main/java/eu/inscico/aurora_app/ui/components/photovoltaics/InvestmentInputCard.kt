package eu.inscico.aurora_app.ui.components.photovoltaics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestmentInputCard(
    currency: String,
    onCalculateCallback: (investmentInput: Int) -> Unit
){

    val investmentInput = remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .clip(shape = RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {

        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(id = R.string.solar_power_description),
            style = MaterialTheme.typography.bodySmall,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        value = investmentInput.value,
        label = {
            Text(text = stringResource(id = R.string.solar_power_investment_title))
        },
        onValueChange = {
            investmentInput.value = it
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
        trailingIcon = { Text(text = currency) }
    )

    Spacer(modifier = Modifier.height(16.dp))

    val buttonColor = if (investmentInput.value.isNotEmpty()) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            enabled = investmentInput.value.isNotEmpty(),
            shape = RoundedCornerShape(32.dp),
            onClick = {
                      onCalculateCallback.invoke(investmentInput.value.toInt())
            },
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
        ) {
            Text(
                text = stringResource(id = R.string.solar_power_investment_button_calculate_title),
                style = MaterialTheme.typography.labelLarge,
                color = Color.White
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}