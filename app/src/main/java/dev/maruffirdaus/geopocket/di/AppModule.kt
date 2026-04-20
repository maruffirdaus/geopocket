package dev.maruffirdaus.geopocket.di

import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module

@Module(includes = [UiModule::class])
@Configuration
class AppModule