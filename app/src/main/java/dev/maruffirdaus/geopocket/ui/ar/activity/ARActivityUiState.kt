package dev.maruffirdaus.geopocket.ui.ar.activity

import dev.maruffirdaus.geopocket.domain.topic.Subtopic
import dev.maruffirdaus.geopocket.ui.ar.activity.model.AngleNodeState
import dev.maruffirdaus.geopocket.ui.ar.activity.model.PointNodeState
import dev.maruffirdaus.geopocket.ui.ar.activity.model.PreviewState
import dev.maruffirdaus.geopocket.ui.ar.activity.model.ReticleNodeState
import dev.maruffirdaus.geopocket.ui.ar.activity.model.SegmentNodeState

data class ARActivityUiState(
    val subtopic: Subtopic = Subtopic.LINE_SEGMENT,
    val reticle: ReticleNodeState? = null,
    val preview: PreviewState? = null,
    val previewEnabled: Boolean = true,
    val planeRendererEnabled: Boolean = true,
    val hitTestIntervalMs: Long = 33L,
    val points: Map<String, PointNodeState> = mapOf(),
    val segments: Map<String, SegmentNodeState> = mapOf(),
    val angles: Map<String, AngleNodeState> = mapOf(),
    val environmentScanned: Boolean = false,
    val completed: Boolean = false
)