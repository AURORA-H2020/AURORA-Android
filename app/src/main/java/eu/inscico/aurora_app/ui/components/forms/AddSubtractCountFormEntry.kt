package eu.inscico.aurora_app.ui.components.forms

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R

@Composable
fun AddSubtractCountFormEntry(
    titleRes: Int,
    isNullCountPossible: Boolean = false,
    initialValue: Int = if (isNullCountPossible) 0 else 1,
    countLimit: Int = 100,
    isRoundedDesign: Boolean = true,
    callback: (Int) -> Unit
) {

    val count = remember {
        mutableStateOf(initialValue)
    }

    count.value = initialValue

    val modifier = if(isRoundedDesign){
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(shape = RoundedCornerShape(16.dp))
    } else {
        Modifier
            .fillMaxWidth()
    }

    Column(
        modifier
    ) {
        ListItem(
            modifier = Modifier
                .fillMaxWidth(),
            trailingContent = {
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    val minusButtonColor = if (isNullCountPossible) {
                        if (count.value > 0)  {
                            ColorFilter.tint(MaterialTheme.colorScheme.primary)
                        } else {
                            ColorFilter.tint(MaterialTheme.colorScheme.onSecondary)
                        }
                    } else {
                        if (count.value > 1)  {
                            ColorFilter.tint(MaterialTheme.colorScheme.primary)
                        } else {
                            ColorFilter.tint(MaterialTheme.colorScheme.onSecondary)
                        }
                    }

                    val plusButtonColor = if(count.value < countLimit){
                        ColorFilter.tint(MaterialTheme.colorScheme.primary)
                    } else {
                        ColorFilter.tint(MaterialTheme.colorScheme.onSecondary)
                    }

                    Image(
                        painter = painterResource(id = R.drawable.baseline_remove_circle_24),
                        contentDescription = "",
                        colorFilter = minusButtonColor,
                        modifier = Modifier
                            .clickable {
                                if (isNullCountPossible) {
                                    count.value = count.value - 1
                                    callback.invoke(count.value)
                                } else if (count.value > 1) {
                                    count.value = count.value - 1
                                    callback.invoke(count.value)
                                }
                            }
                            .padding(8.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = count.value.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Image(
                        painter = painterResource(id = R.drawable.baseline_add_circle_24),
                        contentDescription = "",
                        colorFilter = plusButtonColor,
                        modifier = Modifier
                            .clickable {
                                if(count.value < countLimit){
                                    count.value = count.value + 1
                                    callback.invoke(count.value)
                                }
                            }
                            .padding(8.dp)
                    )
                }
            },
            headlineContent = {
                Text(
                    text = stringResource(id = titleRes)
                )
            }
        )
    }
}