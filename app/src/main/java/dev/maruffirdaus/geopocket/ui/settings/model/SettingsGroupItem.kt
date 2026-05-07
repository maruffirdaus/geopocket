package dev.maruffirdaus.geopocket.ui.settings.model

import androidx.compose.ui.graphics.vector.ImageVector

sealed interface SettingsGroupItem {
    val title: String
    val description: String
    val icon: ImageVector

    data class Action(
        override val title: String,
        override val description: String,
        override val icon: ImageVector,
        val onClick: () -> Unit
    ) : SettingsGroupItem

    data class Switch(
        override val title: String,
        override val description: String,
        override val icon: ImageVector,
        val checked: Boolean,
        val onCheckedChange: (Boolean) -> Unit
    ) : SettingsGroupItem
}