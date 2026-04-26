package dev.maruffirdaus.geopocket.ui.ar

import dev.maruffirdaus.geopocket.domain.topic.Subtopic
import dev.maruffirdaus.geopocket.ui.ar.model.AngleNodeState
import dev.maruffirdaus.geopocket.ui.ar.model.PointNodeState
import dev.maruffirdaus.geopocket.ui.ar.model.PreviewState
import dev.maruffirdaus.geopocket.ui.ar.model.ReticleNodeState
import dev.maruffirdaus.geopocket.ui.ar.model.SegmentNodeState

data class ARUiState(
    val subtopic: Subtopic = Subtopic.LINE_SEGMENT,
    val reticle: ReticleNodeState? = null,
    val preview: PreviewState? = null,
    val previewEnabled: Boolean = true,
    val points: Map<String, PointNodeState> = mapOf(),
    val segments: Map<String, SegmentNodeState> = mapOf(),
    val angles: Map<String, AngleNodeState> = mapOf(),
    val completed: Boolean = false,
    val completionImage: String? = null
)
