package dev.maruffirdaus.geopocket.ui.ar.model

import dev.maruffirdaus.geopocket.domain.topic.constraint.AngleConstraint
import dev.maruffirdaus.geopocket.ui.ar.extension.angleBetween
import dev.maruffirdaus.geopocket.ui.ar.extension.angleBisectorPosition
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.math.Position
import kotlin.math.abs

data class AngleNodeState(
    val id: String,
    val worldPosition: Position = Position(),
    val quaternion: Quaternion = Quaternion(),
    val degree: Float = 0f
) {
    private var constraint: AngleConstraint? = null

    constructor(
        id: String,
        startPos: Position,
        centerPos: Position,
        endPos: Position,
        quaternion: Quaternion,
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
        startPoint: PointNodeState,
        centerPoint: PointNodeState,
        endPoint: PointNodeState,
        constraint: AngleConstraint? = null
    ) : this(
        id = "${startPoint.id}${centerPoint.id}${endPoint.id}",
        worldPosition = centerPoint.worldPosition.angleBisectorPosition(
            startPoint.worldPosition,
            endPoint.worldPosition
        ),
        quaternion = centerPoint.quaternion,
        degree = snapDegreeToTarget(
            centerPoint.worldPosition.angleBetween(
                startPoint.worldPosition,
                endPoint.worldPosition
            ),
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
        id = id,
        startPos = startPos,
        centerPos = centerPos,
        endPos = endPos,
        quaternion = quaternion,
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
