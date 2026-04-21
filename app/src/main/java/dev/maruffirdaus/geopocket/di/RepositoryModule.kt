package dev.maruffirdaus.geopocket.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(includes = [DatabaseModule::class])
@ComponentScan("dev.maruffirdaus.geopocket.data.repository")
class RepositoryModule