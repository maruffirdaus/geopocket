package dev.maruffirdaus.geopocket.ui.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    fun changeSelectedNavItem(navItem: NavItem) {
        _uiState.update {
            it.copy(selectedNavItem = navItem)
        }
    }
}