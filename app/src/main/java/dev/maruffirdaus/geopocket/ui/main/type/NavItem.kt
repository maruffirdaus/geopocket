package dev.maruffirdaus.geopocket.ui.main.type

import androidx.annotation.DrawableRes
import dev.maruffirdaus.geopocket.R

enum class NavItem(
    val label: String,
    @param:DrawableRes val selectedIcon: Int,
    @param:DrawableRes val unselectedIcon: Int
) {
    HOME("Home", R.drawable.ic_house_fill, R.drawable.ic_house),
    SAVED("Saved", R.drawable.ic_camera_fill, R.drawable.ic_camera),
    SETTINGS("Settings", R.drawable.ic_gear_fill, R.drawable.ic_gear),
}