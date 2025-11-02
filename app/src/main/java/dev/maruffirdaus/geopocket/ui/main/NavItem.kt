package dev.maruffirdaus.geopocket.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home)
}