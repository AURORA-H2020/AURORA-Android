package eu.inscico.aurora_app.core

import android.app.Application
import eu.inscico.aurora_app.core.koin.servicesModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {


    // region: Lifecycle
    // ---------------------------------------------------------------------------------------------

    override fun onCreate() {
        super.onCreate()

        // Start Koin
        startKoin{
            androidLogger(Level.ERROR)
            androidContext(this@App)
            modules(servicesModule)
        }
    }

    // endregion

}