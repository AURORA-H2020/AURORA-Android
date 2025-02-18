package eu.inscico.aurora_app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R

@Composable
fun DecoratedHeadline(
    headline: String,
    supportingText: String? = null,
    leadingIconRes: Int? = null,
    leadingIconColor: Color = MaterialTheme.colorScheme.primary,
    actionIconRes: Int? = null,
    onActionClicked: (() -> Unit)? = null
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                leadingIconRes?.let {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        tint = leadingIconColor,
                        painter = painterResource(id = it),
                        contentDescription = ""
                    )
                }

                Spacer(Modifier.width(8.dp))

                Text(
                    text = headline,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

            }

            supportingText?.let {
                Spacer(Modifier.width(8.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        actionIconRes?.let {
            Column(
                modifier = Modifier
                    .size(32.dp)
                    .clip(AbsoluteRoundedCornerShape(16.dp))
                    .clickable {
                        onActionClicked?.invoke()
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
                ) {
                Icon(
                    modifier = Modifier
                        .size(24.dp),
                    painter = painterResource(id = it),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = ""
                )
            }
        }
    }
}