package eu.inscico.aurora_app.ui.screens.photovoltaic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.common.io.Files.append
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

                    val textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSecondary,
                        textAlign = TextAlign.Start,
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight(500),
                        fontSize = 11.sp,
                        lineHeight = 13.sp,
                        letterSpacing = 0.75.sp
                    )

                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = context.getString(
                            R.string.solar_power_investment_description,
                            userCity.value?.name ?: ""
                        ),
                        style = textStyle,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    val annotatedString = buildAnnotatedString {

                        append(stringResource(id = R.string.solar_power_investment_description_with_link_first_part))
                        append(" ")

                        pushStringAnnotation(tag = stringResource(id = R.string.solar_power_investment_description_with_link_link_part), annotation = "https://www.aurora-h2020.eu")
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                            append(stringResource(id = R.string.solar_power_investment_description_with_link_link_part))
                        }
                        pop()

                        append(" ")

                        append(stringResource(id = R.string.solar_power_investment_description_with_link_second_part))
                    }

                    ClickableText(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        text = annotatedString,
                        style = textStyle,
                        onClick = { offset ->
                            annotatedString.getStringAnnotations(tag = context.getString(R.string.solar_power_investment_description_with_link_link_part), start = offset, end = offset).firstOrNull()?.let {
                                ExternalUtils.openBrowser(
                                    context = context,
                                    url = it.item
                                )
                            }
                        })
                }
            }
        }
    }
}