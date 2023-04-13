package eu.inscico.aurora_app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun AppBar(
    modifier: Modifier = Modifier,
    title: String,
    hasBackNavigation: Boolean,
    actionButton: @Composable (ColumnScope.() -> Unit)? = null,
    backNavigationCallback: (() -> Unit)? = null,
    actionButtonCallback: (() -> Unit)? = null
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
    ) {

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface),
            verticalAlignment = Alignment.Bottom,
        ) {

            Spacer(Modifier.height(46.dp))

            if (hasBackNavigation) {
                Image(
                    painter = painterResource(id = eu.inscico.aurora_app.R.drawable.baseline_arrow_back_24),
                    modifier = Modifier
                        .height(48.dp)
                        .width(48.dp)
                        .padding(7.dp)
                        .clickable {
                            backNavigationCallback?.invoke()
                        },
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
            }


            // add title or logo
            Row(modifier = Modifier.padding(7.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (actionButton != null) {
                Column(content = actionButton, modifier = Modifier.clickable {
                    actionButtonCallback?.invoke()
                })
            }
        }
        Spacer(Modifier.height(2.dp))
        Divider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}