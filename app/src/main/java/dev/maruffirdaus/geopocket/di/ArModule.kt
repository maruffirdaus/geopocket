package dev.maruffirdaus.geopocket.di

import dev.maruffirdaus.geopocket.ui.ar.common.util.NodeUtil
import org.koin.dsl.module

val arModule = module {
    single<NodeUtil> { NodeUtil() }
}