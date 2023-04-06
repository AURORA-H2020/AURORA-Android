package eu.inscico.aurora_app.ui.components

import android.content.res.Resources.Theme
import android.graphics.fonts.FontStyle
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun AppBar(
    title: String,
    hasBackNavigation: Boolean,
    actionButton: @Composable (ColumnScope.() -> Unit) ? = null,
    callback: (() -> Unit) ? = null
){

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp, 8.dp, 4.dp, 8.dp).background(MaterialTheme.colorScheme.surface),
        verticalAlignment = Alignment.Bottom,
    ) {


        // add title or logo
        Row(modifier = Modifier.padding(12.dp, 16.dp, 16.dp, 10.dp)) {
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


        if(hasBackNavigation) {
                Column(content = {
                    Image(
                        painter = painterResource(id = eu.inscico.aurora_app.R.drawable.baseline_arrow_back_24),
                        modifier = Modifier
                            .height(44.dp)
                            .padding(7.dp, 11.dp, 7.dp, 11.dp),
                        contentDescription = ""
                    )
                })
            }

            if(actionButton != null){
                Column(content = actionButton, modifier = Modifier.clickable {
                    callback?.invoke()
                })
            }

        }

        // add actions if needed
        //AppBarActionContainer(theme = theme, actions = actions)

}