package eu.inscico.aurora_app.ui.screens.settings.featurePreview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.ui.components.AppBar
import eu.inscico.aurora_app.ui.theme.semiTransparentBackgroundDark
import eu.inscico.aurora_app.ui.theme.semiTransparentBackgroundLight
import org.koin.androidx.compose.get

@OptIn(ExperimentalPagerApi::class)
@Composable
fun FeaturePreviewScreen(
    navigationService: NavigationService = get()
) {

    val sliderElements = listOf(
        R.drawable.aurora_feature_preview_track_energy,
        R.drawable.aurora_feature_preview_data_overview,
        R.drawable.aurora_feature_preview_profile_data
    )

    val sliderElementsDescriptions = listOf(
        R.string.settings_section_feature_preview_track_energy_description,
        R.string.settings_section_feature_preview_data_overview_description,
        R.string.settings_section_feature_preview_profile_data_description
    )

    val pagerState = rememberPagerState(
        initialPage = 0
    )

    val isDescriptionVisible = remember {
        mutableStateOf(true)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {

        AppBar(
            title = stringResource(id = R.string.settings_legal_information_feature_preview_title),
            hasBackNavigation = true,
            backNavigationCallback = {
                navigationService.navControllerTabSettings?.popBackStack()
            }
        )

        Box(
            Modifier
                .fillMaxSize()
                .clickable {
                    isDescriptionVisible.value = !isDescriptionVisible.value
                }) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                HorizontalPager(
                    count = sliderElements.size,
                    state = pagerState,
                    modifier = Modifier.weight(0.9F)
                ) { page ->

                    val currentPhoto = sliderElements[page]

                    Box(
                        Modifier
                            .fillMaxSize()
                            .clickable {
                                isDescriptionVisible.value = !isDescriptionVisible.value
                            }) {


                        Image(
                            painterResource(id = currentPhoto),
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                                .fillMaxSize(),
                            contentDescription = "",
                        )
                    }
                }
                //Horizontal dot indicator
                HorizontalPagerIndicator(
                    activeColor = MaterialTheme.colorScheme.primary,
                    inactiveColor = MaterialTheme.colorScheme.outlineVariant,
                    pagerState = pagerState, modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .weight(0.1F)

                )
            }
            val currentDescription = sliderElementsDescriptions[pagerState.currentPage]
            val backgroundColor = if (isSystemInDarkTheme()) {
                semiTransparentBackgroundDark
            } else {
                semiTransparentBackgroundLight
            }
            if (isDescriptionVisible.value) {
                Box(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(start = 12.dp, end = 12.dp, bottom = 46.dp)
                        .background(
                            backgroundColor, shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Text(
                        text = stringResource(id = currentDescription),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}