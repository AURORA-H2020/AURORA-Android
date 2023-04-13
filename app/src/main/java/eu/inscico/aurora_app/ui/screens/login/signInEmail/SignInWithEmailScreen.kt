package eu.inscico.aurora_app.ui.screens.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.TabRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.ui.components.AppBar
import eu.inscico.aurora_app.ui.screens.login.signInEmail.SignInWithEmailLoginScreen
import eu.inscico.aurora_app.ui.screens.login.signInEmail.SignInWithEmailRegisterScreen
import eu.inscico.aurora_app.ui.screens.login.signInEmail.SignInWithEmailViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SignInWithEmailScreen(
    viewModel: SignInWithEmailViewModel = koinViewModel(),
    navigationService: NavigationService = get()
){

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0)

    Column(modifier = Modifier.fillMaxSize()) {

        AppBar(
            title = stringResource(id = R.string.login_email_sign_in_button_text),
            hasBackNavigation = true,
            backNavigationCallback = {
                navigationService.navControllerAuth?.popBackStack()
            }
        )

        TabRow(
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = MaterialTheme.colorScheme.surface,
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
            count = 3,
            state = pagerState,
        ) { tabIndex ->
            when (tabIndex) {
                0 -> SignInWithEmailLoginScreen(viewModel)
                1 -> SignInWithEmailRegisterScreen(viewModel)
            }
        }
    }
}