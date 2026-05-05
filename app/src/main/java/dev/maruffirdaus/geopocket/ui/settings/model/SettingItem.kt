package dev.maruffirdaus.geopocket.ui.settings.model

import androidx.compose.ui.graphics.vector.ImageVector

data class SettingItem(
    val title: String,
    val description: String,
    val onClick: () -> Unit,
    val icon: ImageVector? = null
)
