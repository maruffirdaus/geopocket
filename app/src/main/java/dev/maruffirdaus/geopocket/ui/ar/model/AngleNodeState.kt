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
        centerPoint: PointNodeState,
        startPos: Position,
        endPos: Position,
        id: String = Uuid.random().toString()
    ) : this(
        id = id,
        worldPosition = centerPoint.angleBisectorPosition(startPos, endPos),
        quaternion = centerPoint.quaternion,
        degree = centerPoint.angleBetween(startPos, endPos)
    )

    fun copy(
        centerPoint: PointNodeState,
        startPos: Position,
        endPos: Position
    ): AngleNodeState =
        this.copy(
            worldPosition = centerPoint.angleBisectorPosition(startPos, endPos),
            quaternion = centerPoint.quaternion,
            degree = centerPoint.angleBetween(startPos, endPos)
        )
}
