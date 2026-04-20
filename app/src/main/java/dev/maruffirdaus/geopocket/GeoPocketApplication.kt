package dev.maruffirdaus.geopocket

import android.app.Application
import dev.maruffirdaus.geopocket.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.annotation.KoinApplication
import org.koin.plugin.module.dsl.startKoin

@KoinApplication(modules = [AppModule::class])
class GeoPocketApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin<GeoPocketApplication> {
            androidLogger()
            androidContext(this@GeoPocketApplication)
        }
    }
}