package eu.inscico.aurora_app.ui.screens.photovoltaic.investments.chart.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.utils.ExternalUtils

@Composable
fun PvProductionInfoDialog(
    plantId: String?,
    showDialog: MutableState<Boolean>,
) {

    val context = LocalContext.current

    if(showDialog.value) {
        Dialog(
            onDismissRequest = { showDialog.value = false },
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    ),
                    text = stringResource(R.string.userdialog_how_does_it_work_info_dialog_title),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    ),
                    text = stringResource(R.string.userdialog_how_does_it_work_info_dialog_description),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )


                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = {
                        showDialog.value = false
                    }) {
                        Text(stringResource(id = R.string.okay))
                    }
                    TextButton(onClick = {
                        val url = "https://aurora-dashboard-git-develop-aurora-h2020.vercel.app/de-DE/pv-data?site=$plantId"
                        ExternalUtils.openBrowser(context, url)
                    }) {
                        Text(stringResource(R.string.userdialog_how_does_it_work_info_dialog_see_full_data_button_title))
                    }
                }
            }
        }
    }


}