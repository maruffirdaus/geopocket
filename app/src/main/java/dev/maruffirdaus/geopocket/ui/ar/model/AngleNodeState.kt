package dev.maruffirdaus.geopocket.ui.ar.model

import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.math.Position
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class AngleNodeState(
    val id: String = Uuid.random().toString(),
    val worldPosition: Position = Position(),
    val quaternion: Quaternion = Quaternion(),
    val degree: Float = 0f
)
