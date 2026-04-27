package dev.maruffirdaus.geopocket.ui.settings.model

data class SettingItem(
    val title: String,
    val description: String,
    val onClick: () -> Unit
)
