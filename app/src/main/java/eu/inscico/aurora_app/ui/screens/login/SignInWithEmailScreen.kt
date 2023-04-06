package eu.inscico.aurora_app.ui.screens.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.ui.components.AppBar
import org.koin.androidx.compose.get

@Composable
fun SignInWithEmailScreen(
    navigationService: NavigationService = get()
){
    Column(modifier = Modifier.fillMaxSize()) {

        AppBar(
            title = stringResource(id = R.string.login_email_sign_in_button_text),
            hasBackNavigation = true,
            callback = {
                navigationService.navControllerAuth?.popBackStack()
            }
        )
    }
}