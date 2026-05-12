package dev.maruffirdaus.geopocket.ui.quiz.result

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class QuizResultViewModel(
    @InjectedParam private val score: Int,
    @InjectedParam private val completed: Boolean
) : ViewModel() {
    private val _uiState = MutableStateFlow(QuizResultUiState(score = score, completed = completed))
    val uiState = _uiState.asStateFlow()
}