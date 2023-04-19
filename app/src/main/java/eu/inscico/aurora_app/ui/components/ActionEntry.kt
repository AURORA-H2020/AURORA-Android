package eu.inscico.aurora_app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.ui.AuroraScaffold
import eu.inscico.aurora_app.ui.theme.AURORAEnergyTrackerTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ActionEntry(
    isNavigation: Boolean,
    iconRes: Int,
    title: String,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    titleColor: Color = MaterialTheme.colorScheme.onSecondary,
    hasIconBackground: Boolean = false,
    callback: (() -> Unit)? = null
) {

    Divider()

    ListItem(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth().clickable {
                callback?.invoke()
            },
        icon = {
            if(hasIconBackground){
                Box(
                    modifier = Modifier
                        .drawBehind {
                            drawRoundRect(
                                Color(iconColor.value).copy(alpha = 0.2F),
                                cornerRadius = CornerRadius(16.dp.toPx())
                            )
                        }
                        .size(30.dp)
                ){
                    Image(
                        modifier = Modifier.matchParentSize().padding(6.dp),
                        painter = painterResource(id = iconRes),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(color = iconColor),
                    )
                }
            } else {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(color = iconColor)
                )
            }
        },
        trailing = {
            if (isNavigation) {
                Image(
                    painter = painterResource(id = R.drawable.outline_keyboard_arrow_right_24),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSecondary)
                )
            }
        },
        text = {
            Text(
                text = title,
                color = titleColor,
                style = TextStyle(
                    color = titleColor,
                    fontSize = 17.sp,
                    lineHeight = 16.sp
                )
            )
        }
    )
}