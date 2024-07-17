package eu.inscico.aurora_app.core.koin

import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import eu.inscico.aurora_app.services.auth.AuthService
import eu.inscico.aurora_app.services.firebase.CloudFunctionsService
import eu.inscico.aurora_app.services.firebase.ConsumptionSummaryService
import eu.inscico.aurora_app.services.firebase.ConsumptionsService
import eu.inscico.aurora_app.services.firebase.CountriesService
import eu.inscico.aurora_app.services.firebase.RecurringConsumptionsService
import eu.inscico.aurora_app.services.firebase.UserService
import eu.inscico.aurora_app.services.jsonParsing.JsonParsingService
import eu.inscico.aurora_app.services.jsonParsing.MoshiJsonParsingService
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.services.network.NetworkService
import eu.inscico.aurora_app.services.notification.NotificationCreationService
import eu.inscico.aurora_app.services.notification.NotificationService
import eu.inscico.aurora_app.services.pvgis.PVGISAPIService
import eu.inscico.aurora_app.services.shared.UnitService
import eu.inscico.aurora_app.services.shared.UserFeedbackService
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val servicesModule = module {

    single { NavigationService() }

    single { UserFeedbackService(_context = androidContext()) }

    single<FirebaseAppCheck> {
        FirebaseApp.initializeApp( androidContext())
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )
        firebaseAppCheck
    }

    factory<FirebaseAuth> {
        FirebaseAuth.getInstance()
    }

    factory<FirebaseFirestore> {
        FirebaseFirestore.getInstance()
    }

    factory<FirebaseRemoteConfig> {
        FirebaseRemoteConfig.getInstance()
    }

    factory<FirebaseFunctions> {
        FirebaseFunctions.getInstance( FirebaseApp.getInstance(),"europe-west3")
    }

    single {
        CloudFunctionsService(context = androidContext(), _functions = get(), _jsonParsingService = get())
    }

    single { AuthService(
        context = androidContext(),
        _firebaseAuth = get(),
        _firestore = get(),
        _userService = get(),
        _countriesService = get(),
        _navigationService = get()
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
            _consumptionsService = get(),
            _consumptionSummariesService = get(),
            _recurringConsumptionsService = get()
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
        ConsumptionsService(_firestore = get(), _firebaseAuth = get(), _networkService = get())
    }

    single {
        ConsumptionSummaryService(
            _firestore = get()
        )
    }


    single {
        RecurringConsumptionsService(
            _firestore = get(),
            _firebaseAuth = get(),
        )
    }

    single {
        PVGISAPIService(
            _jsonParsingService = get()
        )
    }

    single {
        UnitService(
            context = androidContext()
        )
    }

    single {
        NetworkService(
            _context = androidContext()
        )
    }

}