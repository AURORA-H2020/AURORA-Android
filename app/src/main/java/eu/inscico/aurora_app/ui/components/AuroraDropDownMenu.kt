package eu.inscico.aurora_app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

@Composable
fun AuroraDropDownMenu(
    isExpanded: Boolean = false,
    items: List<String>,
    itemSelectedCallback: (String) -> Unit
) {

    val expanded = remember { mutableStateOf(isExpanded) }

    val selectedIndex = remember { mutableStateOf(0) }
    val selectedItem = remember { mutableStateOf(items[selectedIndex.value]) }

    Box {
        Column {
            OutlinedTextField(
                value = (selectedItem.value),
                onValueChange = { },
                label = { Text(text = "My List") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { Icon(Icons.Outlined.ArrowDropDown, null) },
                readOnly = true
            )
            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false },
            ) {
                items.forEach { entry ->

                    DropdownMenuItem(
                        onClick = {
                            selectedItem.value = entry
                            selectedIndex.value = items.indexOf(entry)
                            expanded.value = false
                        },
                        content = {
                            Text(
                                text = (entry),
                                modifier = Modifier.wrapContentWidth())
                        }
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Transparent)
                .padding(10.dp)
                .clickable(
                    onClick = { expanded.value = !expanded.value }
                )
        )
    }

}

