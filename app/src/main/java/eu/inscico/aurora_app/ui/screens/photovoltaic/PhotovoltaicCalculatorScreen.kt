package eu.inscico.aurora_app.ui.screens.photovoltaic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.photovoltaics.PhotovoltaicInvestmentResult
import eu.inscico.aurora_app.services.firebase.CountriesService
import eu.inscico.aurora_app.services.pvgis.PVGISAPIService
import eu.inscico.aurora_app.services.shared.UserFeedbackService
import eu.inscico.aurora_app.ui.components.AppBar
import eu.inscico.aurora_app.ui.components.container.ScrollableContent
import eu.inscico.aurora_app.ui.components.photovoltaics.InvestmentInputCard
import eu.inscico.aurora_app.ui.components.photovoltaics.InvestmentResultCard
import eu.inscico.aurora_app.utils.ExternalUtils
import eu.inscico.aurora_app.utils.KeyboardState
import eu.inscico.aurora_app.utils.keyboardAsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun PhotovoltaicCalculatorScreen(
    viewModel: PhotovoltaicCalculatorViewModel = koinViewModel(),
    userFeedbackService: UserFeedbackService = get()
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    val userCity = viewModel.userCity.observeAsState()

    val investmentResultLive = viewModel.investmentResultLive.observeAsState()

    val investmentResult = remember {
        mutableStateOf<PhotovoltaicInvestmentResult?>(null)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AppBar(
            title = stringResource(id = R.string.solar_power_title),
            hasBackNavigation = false
        )

        Column(
            Modifier.background(MaterialTheme.colorScheme.background)
        ) {

            ScrollableContent(background = MaterialTheme.colorScheme.background) {

                val result = investmentResultLive.value
                if (result != null) {
                    InvestmentResultCard(
                        investmentResult = result,
                        resetCallback = {
                            viewModel.updateInvestmentResult(null)
                        },
                        learnMoreCallback = {
                            ExternalUtils.openBrowser(
                                context = context,
                                url = "https://www.aurora-h2020.eu"
                            )
                        })
                } else {
                    InvestmentInputCard { investmentInput ->

                        keyboardController?.hide()

                        userCity.value?.pvgisParams?.let {
                            userFeedbackService.showLoadingDialog()
                            CoroutineScope(Dispatchers.IO).launch {
                                val peakPower = investmentInput.div(it.investmentFactor).div(1000)
                                val requestResult = viewModel.calculateInvestment(
                                    lat = it.lat,
                                    lon = it.long,
                                    peakpower = peakPower,
                                    angle = it.angle,
                                    aspect = it.aspect
                                )
                                userFeedbackService.hideLoadingDialog()
                                if (requestResult.isSuccessful) {
                                    val body = requestResult.body()
                                    if (body != null) {
                                        val investment = PhotovoltaicInvestmentResult.from(
                                            body,
                                            amount = investmentInput
                                        )
                                        viewModel.updateInvestmentResult(investment)
                                    }

                                } else {

                                }
                            }
                        }
                    }
                }


                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(shape = RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                ) {

                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = context.getString(
                            R.string.solar_power_investment_description,
                            userCity.value?.name ?: ""
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = stringResource(id = R.string.solar_power_investment_description_with_link),
                        style = MaterialTheme.typography.labelSmall,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}