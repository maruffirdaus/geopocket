package dev.maruffirdaus.geopocket.ui.ar.node

import dev.maruffirdaus.geopocket.ui.ar.node.model.CrosshairNode
import dev.maruffirdaus.geopocket.ui.ar.node.model.LineNode
import dev.maruffirdaus.geopocket.ui.ar.node.model.MarkerNode

data class NodeManagerState(
    val crosshairNode: CrosshairNode? = null,
    val lineHelperNode: LineNode? = null,
    val markerNodes: Map<String, MarkerNode> = mapOf(),
    val lineNodes: Map<String, LineNode> = mapOf()
)
