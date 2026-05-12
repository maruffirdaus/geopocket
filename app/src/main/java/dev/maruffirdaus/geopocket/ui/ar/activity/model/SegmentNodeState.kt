package dev.maruffirdaus.geopocket.ui.ar.activity.model

import dev.maruffirdaus.geopocket.domain.settings.SettingItem
import dev.maruffirdaus.geopocket.domain.topic.constraint.SegmentConstraint
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Float4
import dev.romainguy.kotlin.math.Mat4
import dev.romainguy.kotlin.math.Quaternion
import dev.romainguy.kotlin.math.cross
import dev.romainguy.kotlin.math.dot
import dev.romainguy.kotlin.math.length
import dev.romainguy.kotlin.math.normalize
import io.github.sceneview.math.Position
import io.github.sceneview.math.Scale
import kotlin.math.abs

data class SegmentNodeState(
    val id: String,
    val startPointId: String,
    val endPointId: String,
    val worldPosition: Position = Position(),
    val quaternion: Quaternion = Quaternion(),
    val scale: Scale = Scale(),
    val length: Float = 0f
) {
    private var measurementAssist: Boolean = SettingItem.MeasurementAssist.default
    private var constraint: SegmentConstraint? = null

    constructor(
        startPointId: String,
        endPointId: String,
        startPos: Position,
        endPos: Position,
        camPos: Position,
        measurementAssist: Boolean,
        constraint: SegmentConstraint? = null
    ) : this(
        id = startPointId + endPointId,
        startPointId = startPointId,
        endPointId = endPointId,
        worldPosition = (startPos + endPos) / 2f,
        quaternion = calculateQuaternion(startPos, endPos, camPos),
        scale = Float3(length(endPos - startPos), 1f, 1f),
        length = length(endPos - startPos).let {
            if (measurementAssist) snapLengthToTarget(it, constraint) else (it * 100).toInt() / 100f
        }
    ) {
        this.measurementAssist = measurementAssist
        this.constraint = constraint
    }

    fun copy(
        startPos: Position,
        endPos: Position,
        camPos: Position
    ): SegmentNodeState = SegmentNodeState(
        startPointId = startPointId,
        endPointId = endPointId,
        startPos = startPos,
        endPos = endPos,
        camPos = camPos,
        measurementAssist = measurementAssist,
        constraint = constraint
    )

    companion object {
        val Empty = SegmentNodeState("", "", "")

        private fun calculateQuaternion(
            startPos: Position,
            endPos: Position,
            camPos: Position
        ): Quaternion {
            val midPoint = (startPos + endPos) / 2f

            var xAxis = normalize(endPos - startPos)

            val toCamera = normalize(camPos - midPoint)

            var yAxis = cross(toCamera, xAxis)
            yAxis = normalize(yAxis)

            var zAxis = cross(xAxis, yAxis)
            zAxis = normalize(zAxis)

            val worldUp = Float3(0f, 1f, 0f)

            if (dot(yAxis, worldUp) < 0f) {
                yAxis = -yAxis
                xAxis = -xAxis
            }

            val rotationMatrix = Mat4(
                Float4(xAxis, 0f),
                Float4(yAxis, 0f),
                Float4(zAxis, 0f),
                Float4(0f, 0f, 0f, 1f)
            )

            return rotationMatrix.toQuaternion()
        }

        private fun snapLengthToTarget(length: Float, constraint: SegmentConstraint?): Float {
            val snappedToMin =
                constraint?.minLength?.takeIf { abs(length - it) <= SegmentConstraint.TOLERANCE }
            val snappedToMax =
                constraint?.maxLength?.takeIf { abs(length - it) <= SegmentConstraint.TOLERANCE }
            return snappedToMin ?: snappedToMax ?: length
        }
    }
}