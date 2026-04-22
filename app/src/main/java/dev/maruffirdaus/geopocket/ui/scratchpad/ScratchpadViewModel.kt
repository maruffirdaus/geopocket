package dev.maruffirdaus.geopocket.ui.scratchpad

import androidx.ink.strokes.Stroke
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class ScratchpadViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ScratchpadUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: ScratchpadEvent) {
        when(event) {
            is ScratchpadEvent.OnStrokesFinished -> onStrokesFinished(event.strokes)
        }
    }

    private fun onStrokesFinished(strokes: List<Stroke>) {
        _uiState.update {
            it.copy(strokes = it.strokes + strokes)
        }
    }
}