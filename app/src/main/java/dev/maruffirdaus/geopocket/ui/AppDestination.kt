package dev.maruffirdaus.geopocket.ui

import kotlinx.serialization.Serializable

@Serializable
sealed class AppDestination {
    @Serializable
    object Main : AppDestination()

    @Serializable
    object ArLine : AppDestination()
}