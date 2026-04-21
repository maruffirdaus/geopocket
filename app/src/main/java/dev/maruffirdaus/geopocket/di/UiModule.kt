package dev.maruffirdaus.geopocket.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(includes = [RepositoryModule::class])
@ComponentScan("dev.maruffirdaus.geopocket.ui")
class UiModule