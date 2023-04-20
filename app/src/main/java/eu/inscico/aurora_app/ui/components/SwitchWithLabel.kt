package eu.inscico.aurora_app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwitchWithLabel(label: String, state: Boolean, onStateChange: (Boolean) -> Unit) {

    val interactionSource = remember { MutableInteractionSource() }

    ListItem(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth(),
        headlineContent = { androidx.compose.material3.Text(text = label) },
        trailingContent = {

            Row(
                modifier = Modifier
                    .clickable(
                        interactionSource = interactionSource,
                        // This is for removing ripple when Row is clicked
                        indication = null,
                        role = Role.Switch,
                        onClick = {
                            onStateChange(!state)
                        }
                    ),
                horizontalArrangement = Arrangement.End,
            ) {
                Switch(
                    checked = state,
                    onCheckedChange = {
                        onStateChange(it)
                    },
                    colors = SwitchDefaults.colors()
                )
            }
        }
    )
}