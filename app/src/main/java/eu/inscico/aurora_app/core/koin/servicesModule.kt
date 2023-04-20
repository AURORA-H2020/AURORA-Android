package eu.inscico.aurora_app.core.koin

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import eu.inscico.aurora_app.services.*
import eu.inscico.aurora_app.services.auth.AuthService
import eu.inscico.aurora_app.services.jsonParsing.JsonParsingService
import eu.inscico.aurora_app.services.jsonParsing.MoshiJsonParsingService
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.services.notification.NotificationCreationService
import eu.inscico.aurora_app.services.notification.NotificationService
import eu.inscico.aurora_app.services.shared.UserFeedbackService
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val servicesModule = module {

    single { NavigationService() }

    single { UserFeedbackService(_context = androidContext()) }


    factory<FirebaseAuth> {
        FirebaseAuth.getInstance()
    }

    factory<FirebaseFirestore> {
        FirebaseFirestore.getInstance()
    }

    single { AuthService(
        context = androidContext(),
        _firebaseAuth = get(),
        _firestore = get(),
        _userService = get(),
        _countriesService = get()
    ) }

    single {
        CountriesService(
            _firestore = get()
        )
    }

    single<JsonParsingService> {
        MoshiJsonParsingService(
            dateFormatPattern = "yyyy-MM-dd'T'HH:mm:ss'.'SSS'Z'"
        )
    }

    single {
        UserService(
            _firestore = get(),
            _firebaseAuth = get(),
            _countryService = get(),
            _consumptionsService = get()
        )
    }

    single {
        NotificationService(
            context = androidContext(),
            _jsonParsingService = get()
        )
    }

    single {
        NotificationCreationService(androidContext())
    }

    single {
        ConsumptionsService(_firestore = get(), _firebaseAuth = get())
    }

}