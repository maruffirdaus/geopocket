package dev.maruffirdaus.geopocket.ui.ar.result

import androidx.lifecycle.ViewModel
import dev.maruffirdaus.geopocket.domain.topic.Subtopic
import dev.maruffirdaus.geopocket.domain.topic.result.AngleResult
import dev.maruffirdaus.geopocket.domain.topic.result.SegmentResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class ARResultViewModel(
    @InjectedParam private val subtopic: Subtopic,
    @InjectedParam private val segments: Map<String, SegmentResult>,
    @InjectedParam private val angles: Map<String, AngleResult>,
    @InjectedParam private val completionImage: String
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        ARResultUiState(
            subtopic = subtopic,
            segments = segments,
            angles = angles,
            completionImage = completionImage
        )
    )
    val uiState = _uiState.asStateFlow()
}