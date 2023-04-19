package eu.inscico.aurora_app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    title: String,
    hasBackNavigation: Boolean,
    actionButton: @Composable() (RowScope.() -> Unit)? = null,
    backNavigationCallback: (() -> Unit)? = null,
    actionButtonCallback: (() -> Unit)? = null
) {

    MediumTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        navigationIcon = {
            if (hasBackNavigation) {
                Image(
                    painter = painterResource(id = eu.inscico.aurora_app.R.drawable.baseline_arrow_back_24),
                    modifier = Modifier

                        .size(42.dp)
                        .padding(horizontal = 7.dp)
                        .clickable {
                            backNavigationCallback?.invoke()
                        },
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
            }
        },
        actions = {

            if (actionButton != null) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    content = actionButton,
                    modifier = Modifier.clickable {
                        actionButtonCallback?.invoke()
                    }
                )
            }
        },
    )
    Divider()

/*
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
                // add title or logo
                Row(modifier = Modifier.padding(horizontal = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Image(
                        painter = painterResource(id = eu.inscico.aurora_app.R.drawable.baseline_arrow_back_24),
                        modifier = Modifier

                            .size(42.dp)
                            .padding(horizontal = 7.dp)
                            .clickable {
                                backNavigationCallback?.invoke()
                            },
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                    )
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.weight(1f),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                }
            } else {
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

 */
}