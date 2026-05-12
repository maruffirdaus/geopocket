package dev.maruffirdaus.geopocket.ui.ar.activity.model

import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.math.Position

data class ReticleNodeState(
    val worldPosition: Position = Position(),
    val quaternion: Quaternion = Quaternion()
)