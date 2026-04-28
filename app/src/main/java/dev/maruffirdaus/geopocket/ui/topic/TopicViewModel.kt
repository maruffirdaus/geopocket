package dev.maruffirdaus.geopocket.ui.topic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.maruffirdaus.geopocket.data.repository.TopicRepository
import dev.maruffirdaus.geopocket.domain.topic.Topic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class TopicViewModel(
    @InjectedParam private val topic: Topic,
    private val topicRepository: TopicRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TopicUiState(topic = topic))
    val uiState = _uiState
        .onStart { refreshSubtopicProgresses() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(1000L),
            TopicUiState(topic = topic)
        )

    private fun refreshSubtopicProgresses() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(subtopicProgresses = topicRepository.getSubtopicProgresses())
            }
        }
    }
}