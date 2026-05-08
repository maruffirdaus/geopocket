package dev.maruffirdaus.geopocket.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.maruffirdaus.geopocket.data.repository.TopicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class HomeViewModel(
    private val topicRepository: TopicRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState
        .onStart { refreshSubtopicProgresses() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(1000L),
            _uiState.value
        )

    private fun refreshSubtopicProgresses() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(subtopicProgresses = topicRepository.getSubtopicProgresses())
            }
        }
    }
}