package dev.maruffirdaus.geopocket.ui.ar.line

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ArLineViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ArLineUiState())
    val uiState = _uiState.asStateFlow()
}