package eu.inscico.aurora_app.ui.components.consumptions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.ui.components.ActionEntry
import eu.inscico.aurora_app.ui.theme.primary
import eu.inscico.aurora_app.utils.ExternalUtils

@Composable
fun DuplicateConsumptionButton(
    modifier: Modifier,
    clickedCallback: () -> Unit
) {

    Column(
        modifier.fillMaxWidth().clip(shape = RoundedCornerShape(16.dp))
    ) {

        ActionEntry(
            title = stringResource(id = R.string.home_duplicate_consumption_button_title),
            iconRes = R.drawable.outline_auto_awesome_motion_24,
            iconColor = primary,
            titleColor = primary,
            isNavigation = false,
            callback = {
                clickedCallback.invoke()
            }
        )

    }
}
