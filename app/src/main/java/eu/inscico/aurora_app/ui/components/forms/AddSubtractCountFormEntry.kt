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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R

@Composable
fun AddSubtractCountFormEntry(
    titleRes: Int,
    isNullCountPossible: Boolean = false,
    initialValue: Int = if(isNullCountPossible) 0 else 1,
    callback: (Int) -> Unit
){

    val context = LocalContext.current

    val count = remember {
        mutableStateOf(initialValue)
    }

    val buttonBackground = MaterialTheme.colorScheme.outlineVariant

    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSecondary,
                shape = RoundedCornerShape(4.dp)
            ),
        trailingContent = {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .drawBehind {
                            drawRoundRect(
                                Color(buttonBackground.value).copy(alpha = 0.2F),
                                cornerRadius = CornerRadius(4.dp.toPx())
                            )
                        }
                        .size(30.dp)
                        .clickable {
                            count.value = count.value + 1
                            callback.invoke(count.value)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_add_24),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSecondary),
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Box(
                    modifier = Modifier
                        .drawBehind {
                            drawRoundRect(
                                Color(buttonBackground.value).copy(alpha = 0.2F),
                                cornerRadius = CornerRadius(4.dp.toPx())
                            )
                        }
                        .clickable {
                            if (isNullCountPossible){
                                count.value = count.value - 1
                                callback.invoke(count.value)
                            } else if(count.value > 1) {
                                count.value = count.value - 1
                                callback.invoke(count.value)
                            }
                        }
                        .size(30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_substract_24),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSecondary),
                    )
                }
            }
        },
        headlineContent = {
            Text(
                text = context.getString(
                    titleRes,
                    count.value
                )
            )
        }
    )
}