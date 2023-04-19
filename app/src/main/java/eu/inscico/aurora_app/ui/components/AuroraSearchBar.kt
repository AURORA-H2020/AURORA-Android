package eu.inscico.aurora_app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R

@Composable
fun AuroraSearchBar(
    initialSearchQuery: String = "",
    searchCallback: (String) -> Unit
) {

    val searchQuery = remember {
        mutableStateOf(initialSearchQuery)
    }

    Column(
        Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = searchQuery.value,
            label = {
                Text(text = stringResource(id = R.string.search))
            },
            onValueChange = {
                searchQuery.value = it
                searchCallback.invoke(it)
            },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.baseline_search_24),
                    modifier = Modifier
                        .size(42.dp)
                        .padding(horizontal = 7.dp),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
            },
            trailingIcon = {
                if (searchQuery.value.isNotEmpty()) {
                    Image(
                        painter = painterResource(id = R.drawable.round_close_24),
                        modifier = Modifier
                            .size(42.dp)
                            .padding(horizontal = 7.dp)
                            .clickable {
                                searchQuery.value = ""
                                searchCallback.invoke("")
                            },
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        )
    }
}