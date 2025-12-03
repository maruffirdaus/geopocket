package dev.maruffirdaus.geopocket.ui.main

import dev.maruffirdaus.geopocket.ui.main.model.NavItem

data class MainUiState(
    val selectedNavItem: NavItem = NavItem.HOME
)
