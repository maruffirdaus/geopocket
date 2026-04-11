package dev.maruffirdaus.geopocket.ui.main

import androidx.lifecycle.ViewModel
import dev.maruffirdaus.geopocket.ui.main.model.MainNavItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.OnSelectedNavItemUpdate -> updateSelectedNavItem(event.navItem)
        }
    }

    private fun updateSelectedNavItem(navItem: MainNavItem) {
        _uiState.update {
            it.copy(selectedNavItem = navItem)
        }
    }
}