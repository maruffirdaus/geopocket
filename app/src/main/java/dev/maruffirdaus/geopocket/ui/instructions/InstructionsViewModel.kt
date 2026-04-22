package dev.maruffirdaus.geopocket.ui.instructions

import androidx.lifecycle.ViewModel
import dev.maruffirdaus.geopocket.domain.topic.Subtopic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class InstructionsViewModel(
    @InjectedParam private val subtopic: Subtopic
) : ViewModel() {
    private val _uiState = MutableStateFlow(InstructionsUiState(subtopic = subtopic))
    val uiState = _uiState.asStateFlow()
}