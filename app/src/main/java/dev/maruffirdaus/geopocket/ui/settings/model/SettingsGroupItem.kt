package dev.maruffirdaus.geopocket.ui.settings.model

sealed interface SettingsGroupItem {
    val title: String
    val description: String
}