package dev.maruffirdaus.geopocket.ui.ar

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ArViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ArUiState())
    val uiState = _uiState.asStateFlow()

    fun changeErrorMessage(errorMessage: String?) {
        _uiState.update {
            it.copy(errorMessage = errorMessage)
        }
    }
}