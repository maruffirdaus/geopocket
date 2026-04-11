package dev.maruffirdaus.geopocket.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppNavKey : NavKey {
    @Serializable
    object Main : AppNavKey

    @Serializable
    data class AR(
        val mode: String
    ) : AppNavKey
}