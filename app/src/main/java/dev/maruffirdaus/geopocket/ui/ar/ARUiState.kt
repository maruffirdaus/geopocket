package dev.maruffirdaus.geopocket.ui.ar

import dev.maruffirdaus.geopocket.ui.ar.model.MarkerNode
import dev.maruffirdaus.geopocket.ui.ar.model.LineNode
import dev.maruffirdaus.geopocket.ui.ar.model.PlacementIndicatorNode

data class ARUiState(
    val placementIndicator: PlacementIndicatorNode? = null,
    val previewLine: LineNode? = null,
    val markers: Map<String, MarkerNode> = mapOf(),
    val measurementLines: Map<String, LineNode> = mapOf(),
    val errorMessage: String? = null
)
