package dev.maruffirdaus.geopocket.ui.settings

import dev.maruffirdaus.geopocket.domain.settings.SettingItem

data class SettingsUiState(
    val checked: Map<SettingItem.Switch, Boolean> = mapOf()
)
