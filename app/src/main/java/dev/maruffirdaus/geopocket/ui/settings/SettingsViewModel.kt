package dev.maruffirdaus.geopocket.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.maruffirdaus.geopocket.data.repository.SettingsRepository
import dev.maruffirdaus.geopocket.data.repository.TopicRepository
import dev.maruffirdaus.geopocket.domain.settings.SettingItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class SettingsViewModel(
    private val topicRepository: TopicRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState
        .onStart { refreshSettings() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(1000L),
            SettingsUiState()
        )

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.OnSwitchChanged -> onSwitchChanged(event.item, event.checked)
            SettingsEvent.OnResetProgress -> onResetProgress()
        }
    }

    private fun onSwitchChanged(item: SettingItem.Switch, checked: Boolean) {
        viewModelScope.launch {
            settingsRepository.saveBoolean(item, checked)
            _uiState.update {
                it.copy(checked = it.checked + (item to checked))
            }
        }
    }

    private fun onResetProgress() {
        viewModelScope.launch {
            topicRepository.deleteSubtopicProgresses()
        }
    }

    private fun refreshSettings() {
        val checked = mutableMapOf<SettingItem.Switch, Boolean>()
        runBlocking(Dispatchers.IO) {
            SettingItem.saveableBoolean.forEach {
                checked[it] = settingsRepository.getBoolean(it)
            }
        }
        _uiState.update {
            it.copy(checked = checked)
        }
    }
}