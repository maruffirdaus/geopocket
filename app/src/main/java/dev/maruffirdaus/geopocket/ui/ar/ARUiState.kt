package dev.maruffirdaus.geopocket.ui.ar

import dev.maruffirdaus.geopocket.ui.ar.model.PointNode
import dev.maruffirdaus.geopocket.ui.ar.model.SegmentNode

data class ARUiState(
    val previewSegment: SegmentNode? = null,
    val points: Map<String, PointNode> = mapOf(),
    val segments: Map<String, SegmentNode> = mapOf(),
    val errorMessage: String? = null
)
