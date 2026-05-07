package dev.maruffirdaus.geopocket.ui.settings.model

data class ActionSettingsGroupItem(
    override val title: String,
    override val description: String,
    val onClick: () -> Unit
) : SettingsGroupItem
