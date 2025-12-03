package dev.maruffirdaus.geopocket.ui.ar.node

import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.node.Node
import io.github.sceneview.node.ViewNode2

data class NodeManagerState(
    val crosshairNode: ViewNode2? = null,
    val lineHelperNode: Node? = null,
    val markerNodes: List<AnchorNode> = listOf(),
    val lineNodes: List<Node> = listOf(),
    val lineLabelNodes: List<ViewNode2> = listOf()
)
