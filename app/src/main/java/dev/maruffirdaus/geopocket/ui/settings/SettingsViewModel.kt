package dev.maruffirdaus.geopocket.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.maruffirdaus.geopocket.data.repository.TopicRepository
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class SettingsViewModel(
    private val topicRepository: TopicRepository
) : ViewModel() {
    fun onEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.OnResetProgress -> onResetProgress()
        }
    }

    private fun onResetProgress() {
        viewModelScope.launch {
            topicRepository.deleteSubtopicProgresses()
        }
    }
}