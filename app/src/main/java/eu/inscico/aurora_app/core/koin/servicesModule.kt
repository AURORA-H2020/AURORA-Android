package eu.inscico.aurora_app.core.koin

import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.services.shared.UserFeedbackService
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val servicesModule = module {

    single { NavigationService() }

    single { UserFeedbackService(_context = androidContext()) }

}