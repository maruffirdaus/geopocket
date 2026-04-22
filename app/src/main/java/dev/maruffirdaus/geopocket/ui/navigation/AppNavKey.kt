package dev.maruffirdaus.geopocket.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppNavKey : NavKey {
    @Serializable
    object Home : AppNavKey

    @Serializable
    data class Topic(
        val topic: String
    ) : AppNavKey

    @Serializable
    data class Instructions(
        val subtopic: String
    ) : AppNavKey

    @Serializable
    data class AR(
        val subtopic: String
    ) : AppNavKey

    @Serializable
    object Achievement : AppNavKey

    @Serializable
    object Settings : AppNavKey
}