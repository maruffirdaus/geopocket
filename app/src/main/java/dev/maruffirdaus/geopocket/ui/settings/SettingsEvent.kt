package dev.maruffirdaus.geopocket.ui.settings

sealed interface SettingsEvent {
    object OnResetProgress : SettingsEvent
}