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
) {
    constructor(
        start: PointNodeState,
        center: PointNodeState,
        end: PointNodeState,
        id: String = Uuid.random().toString()
    ) : this(
        id = id,
        worldPosition = center.angleBisectorPosition(start, end),
        quaternion = center.quaternion,
        degree = center.angleBetween(start, end)
    )

    fun copy(start: PointNodeState, center: PointNodeState, end: PointNodeState): AngleNodeState =
        this.copy(
            worldPosition = center.angleBisectorPosition(start, end),
            quaternion = center.quaternion,
            degree = center.angleBetween(start, end)
        )
}
