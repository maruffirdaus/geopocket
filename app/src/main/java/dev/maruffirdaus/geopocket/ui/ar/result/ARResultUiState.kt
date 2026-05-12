package dev.maruffirdaus.geopocket.ui.ar.result

import dev.maruffirdaus.geopocket.domain.topic.Subtopic
import dev.maruffirdaus.geopocket.domain.topic.result.AngleResult
import dev.maruffirdaus.geopocket.domain.topic.result.SegmentResult

data class ARResultUiState(
    val subtopic: Subtopic = Subtopic.LINE_SEGMENT,
    val segments: Map<String, SegmentResult> = mapOf(),
    val angles: Map<String, AngleResult> = mapOf(),
    val completionImage: String = ""
)
