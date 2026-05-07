package dev.maruffirdaus.geopocket.ui.settings.model

data class SwitchSettingsGroupItem(
    override val title: String,
    override val description: String,
    val checked: Boolean,
    val onCheckedChange: (Boolean) -> Unit
) : SettingsGroupItem
