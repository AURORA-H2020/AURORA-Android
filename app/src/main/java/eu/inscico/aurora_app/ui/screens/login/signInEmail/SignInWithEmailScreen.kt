package eu.inscico.aurora_app.ui.screens.login.signInEmail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.services.navigation.NavigationService
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SignInWithEmailScreen(
    viewModel: SignInWithEmailViewModel = koinViewModel(),
    navigationService: NavigationService = get()
){

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0)

    Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {

        TopAppBar(
            title = {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    Text(
                        text = stringResource(id = R.string.login_email_sign_in_button_text),
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            navigationIcon = {

                    Image(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        modifier = Modifier

                            .size(42.dp)
                            .padding(horizontal = 7.dp)
                            .clickable {
                                navigationService.navControllerAuth?.popBackStack()
                            },
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                    )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Tab(
                selected = pagerState.currentPage == 0,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                },
                text = {
                    androidx.compose.material3.Text(text = stringResource(id = R.string.sign_in_with_email_tab_bar_login))
                },
                selectedContentColor = MaterialTheme.colorScheme.primary,
            )
            Tab(
                selected = pagerState.currentPage == 1,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                },
                text = {
                    androidx.compose.material3.Text(text = stringResource(id = R.string.sign_in_with_email_tab_bar_register))
                },
            )
        }

        HorizontalPager(
            count = 2,
            state = pagerState,
        ) { tabIndex ->
            when (tabIndex) {
                0 -> SignInWithEmailLoginScreen(viewModel)
                1 -> SignInWithEmailRegisterScreen(viewModel)
            }
        }
    }
}