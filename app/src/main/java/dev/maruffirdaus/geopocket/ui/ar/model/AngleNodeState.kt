package dev.maruffirdaus.geopocket.ui.ar.model

import dev.maruffirdaus.geopocket.domain.topic.constraint.AngleConstraint
import dev.maruffirdaus.geopocket.ui.ar.extension.angleBetween
import dev.maruffirdaus.geopocket.ui.ar.extension.angleBisectorPosition
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.math.Position
import kotlin.math.abs
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class AngleNodeState(
    val id: String = Uuid.random().toString(),
    val worldPosition: Position = Position(),
    val quaternion: Quaternion = Quaternion(),
    val degree: Float = 0f
) {
    private var constraint: AngleConstraint? = null

    constructor(
        startPos: Position,
        centerPos: Position,
        endPos: Position,
        quaternion: Quaternion,
        id: String = Uuid.random().toString(),
        constraint: AngleConstraint? = null
    ) : this(
        id = id,
        worldPosition = centerPos.angleBisectorPosition(startPos, endPos),
        quaternion = quaternion,
        degree = snapDegreeToTarget(centerPos.angleBetween(startPos, endPos), constraint)
    ) {
        this.constraint = constraint
    }

    constructor(
        centerPoint: PointNodeState,
        startPos: Position,
        endPos: Position,
        constraint: AngleConstraint? = null
    ) : this(
        id = centerPoint.id,
        worldPosition = centerPoint.worldPosition.angleBisectorPosition(startPos, endPos),
        quaternion = centerPoint.quaternion,
        degree = snapDegreeToTarget(
            centerPoint.worldPosition.angleBetween(startPos, endPos),
            constraint
        )
    ) {
        this.constraint = constraint
    }

    fun copy(
        startPos: Position,
        centerPos: Position,
        endPos: Position
    ): AngleNodeState = AngleNodeState(
        startPos = startPos,
        centerPos = centerPos,
        endPos = endPos,
        quaternion = quaternion,
        id = id,
        constraint = constraint
    )

    companion object {
        private fun snapDegreeToTarget(degree: Float, constraint: AngleConstraint?): Float {
            val snappedToMin =
                constraint?.minDegree?.takeIf { abs(degree - it) <= constraint.tolerance }
            val snappedToMax =
                constraint?.maxDegree?.takeIf { abs(degree - it) <= constraint.tolerance }
            return snappedToMin ?: snappedToMax ?: degree
        }
    }
}
