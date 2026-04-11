package dev.maruffirdaus.geopocket.ui.ar.model

import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.math.Position
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class MarkerNode(
    val id: String = Uuid.random().toString(),
    val worldPosition: Position = Position(),
    val quaternion: Quaternion = Quaternion(),
    val connectedLineIds: List<String> = listOf()
)
