package dev.maruffirdaus.geopocket.ui.ar.line

import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.node.Node

data class ArLineUiState(
    val markerNodes: List<AnchorNode> = listOf(),
    val lineNodes: List<Node> = listOf()
)
