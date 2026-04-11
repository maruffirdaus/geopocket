package dev.maruffirdaus.geopocket.ui.main

import dev.maruffirdaus.geopocket.ui.main.model.MainNavItem

sealed interface MainEvent {
    data class OnSelectedNavItemUpdate(val navItem: MainNavItem) : MainEvent
}