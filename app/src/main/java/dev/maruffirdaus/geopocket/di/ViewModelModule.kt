package dev.maruffirdaus.geopocket.di

import dev.maruffirdaus.geopocket.ui.ar.line.ArLineViewModel
import dev.maruffirdaus.geopocket.ui.home.HomeViewModel
import dev.maruffirdaus.geopocket.ui.main.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainViewModel() }
    viewModel { HomeViewModel() }
    viewModel { ArLineViewModel() }
}