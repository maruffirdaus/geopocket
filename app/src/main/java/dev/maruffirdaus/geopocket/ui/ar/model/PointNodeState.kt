package dev.maruffirdaus.geopocket.ui.ar.model

import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.math.Position

data class PointNodeState(
    val id: String,
    val worldPosition: Position = Position(),
    val quaternion: Quaternion = Quaternion(),
    val connectedSegmentIds: Set<String> = setOf()
)
