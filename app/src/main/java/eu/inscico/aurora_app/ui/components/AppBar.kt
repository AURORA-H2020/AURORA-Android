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
) {

    TopAppBar(
        title = {
            val horizontalArrangement = if(hasBackNavigation){
                Arrangement.Start
            } else {
                Arrangement.Center
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = horizontalArrangement) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
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
                    content = actionButton
                )
            }
        },
    )
    Divider()
}