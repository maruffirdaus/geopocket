package dev.maruffirdaus.geopocket.ui.settings

import dev.maruffirdaus.geopocket.domain.settings.SettingItem

sealed interface SettingsEvent {
    data class OnSwitchChanged(val item: SettingItem.Switch, val checked: Boolean) : SettingsEvent
    object OnResetProgress : SettingsEvent
}