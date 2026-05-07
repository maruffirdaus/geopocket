package dev.maruffirdaus.geopocket.ui.ar.model

import dev.maruffirdaus.geopocket.domain.settings.SettingItem
import dev.maruffirdaus.geopocket.domain.topic.constraint.AngleConstraint
import dev.maruffirdaus.geopocket.ui.ar.extension.angleBetween
import dev.maruffirdaus.geopocket.ui.ar.extension.angleBisectorPosition
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.math.Position
import kotlin.math.abs

@ConsistentCopyVisibility
data class AngleNodeState private constructor(
    val id: String,
    val worldPosition: Position = Position(),
    val quaternion: Quaternion = Quaternion(),
    val degree: Float = 0f,
    private val measurementAssist: Boolean = SettingItem.MeasurementAssist.default,
    private val constraint: AngleConstraint? = null
) {
    constructor(
        id: String,
        startPos: Position,
        centerPos: Position,
        endPos: Position,
        quaternion: Quaternion,
        measurementAssist: Boolean,
        constraint: AngleConstraint? = null
    ) : this(
        id = id,
        worldPosition = centerPos.angleBisectorPosition(startPos, endPos),
        quaternion = quaternion,
        degree = centerPos.angleBetween(startPos, endPos).let {
            if (measurementAssist) snapDegreeToTarget(it, constraint) else it
        },
        measurementAssist = measurementAssist,
        constraint = constraint
    )

    constructor(
        startPoint: PointNodeState,
        centerPoint: PointNodeState,
        endPoint: PointNodeState,
        measurementAssist: Boolean,
        constraint: AngleConstraint? = null
    ) : this(
        id = "${startPoint.id}${centerPoint.id}${endPoint.id}",
        worldPosition = centerPoint.worldPosition.angleBisectorPosition(
            startPoint.worldPosition,
            endPoint.worldPosition
        ),
        quaternion = centerPoint.quaternion,
        degree = centerPoint.worldPosition.angleBetween(
            startPoint.worldPosition,
            endPoint.worldPosition
        ).let {
            if (measurementAssist) snapDegreeToTarget(it, constraint) else it
        },
        measurementAssist = measurementAssist,
        constraint = constraint
    )

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
        measurementAssist = measurementAssist,
        constraint = constraint
    )

    companion object {
        val Empty = AngleNodeState("")

        private fun snapDegreeToTarget(degree: Float, constraint: AngleConstraint?): Float {
            val snappedToMin =
                constraint?.minDegree?.takeIf { abs(degree - it) <= constraint.tolerance }
            val snappedToMax =
                constraint?.maxDegree?.takeIf { abs(degree - it) <= constraint.tolerance }
            return snappedToMin ?: snappedToMax ?: degree
        }
    }
}
