package eu.inscico.aurora_app.ui.screens.settings.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.consumptions.ElectricitySource
import eu.inscico.aurora_app.model.consumptions.ElectricitySource.Companion.getDisplayName
import eu.inscico.aurora_app.model.user.RegionEnum
import eu.inscico.aurora_app.model.user.RegionEnum.Companion.getDisplayName
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.services.shared.UserFeedbackService
import eu.inscico.aurora_app.ui.components.AppBar
import eu.inscico.aurora_app.ui.components.forms.SpinnerFormEntry
import eu.inscico.aurora_app.ui.components.forms.SpinnerItem
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

@Composable
fun SelectRegionScreen(
    viewModel: SelectRegionViewModel = koinViewModel(),
    navigationService: NavigationService = get(),
    userFeedbackService: UserFeedbackService = get()
) {

    val context = LocalContext.current

    val isSaveValid = remember {
        mutableStateOf(false)
    }

    val selectedRegion = remember {
        mutableStateOf<RegionEnum>(viewModel.getSavedRegion(context))
    }

    val buttonColor = if (isSaveValid.value) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        AppBar(
            title = stringResource(id = R.string.settings_profile_select_region_title),
            hasBackNavigation = true,
            backNavigationCallback = {
                navigationService.navControllerTabSettings?.popBackStack()
            }
        )

        Column(Modifier.verticalScroll(rememberScrollState())) {

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(shape = RoundedCornerShape(16.dp)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(Modifier.height(32.dp))

                val allRegionSpinnerItems = viewModel.allSelectableRegions.map {
                    SpinnerItem.Entry(name = it.getDisplayName(context), data = it)
                }

                val selectedEntry = SpinnerItem.Entry(
                    name = selectedRegion.value.getDisplayName(context),
                    data = selectedRegion.value
                )

                SpinnerFormEntry(
                    title = stringResource(id = R.string.settings_profile_select_region_title),
                    selectedEntry = selectedEntry,
                    allEntries = allRegionSpinnerItems,
                    callback = { item, _ ->
                        selectedRegion.value = item.data as RegionEnum
                        isSaveValid.value = selectedRegion.value != viewModel.getSavedRegion(context)
                    }
                )

                Spacer(Modifier.height(32.dp))

                Button(
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .fillMaxWidth(),
                    enabled = isSaveValid.value,
                    shape = RoundedCornerShape(32.dp),
                    onClick = {
                        viewModel.saveRegion(context, selectedRegion.value)
                        navigationService.navControllerTabSettings?.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                ) {
                    Text(
                        text = stringResource(id = R.string.settings_edit_profile_submit_button_title),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = stringResource(id = R.string.settings_profile_select_region_info_description),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onSecondary
                )

            }
        }
    }
}