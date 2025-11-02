package dev.maruffirdaus.geopocket

import android.app.Application
import dev.maruffirdaus.geopocket.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class GeoPocketApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@GeoPocketApplication)
            modules(viewModelModule)
        }
    }
}