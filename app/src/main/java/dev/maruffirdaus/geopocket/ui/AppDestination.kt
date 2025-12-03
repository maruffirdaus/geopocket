package dev.maruffirdaus.geopocket.ui

import kotlinx.serialization.Serializable

@Serializable
sealed class AppDestination {
    @Serializable
    object Main : AppDestination()

    @Serializable
    data class Ar(
        val mode: String
    ) : AppDestination()
}