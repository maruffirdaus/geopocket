package dev.maruffirdaus.geopocket.ui.ar.model

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
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class SegmentNodeState(
    val id: String = Uuid.random().toString(),
    val worldPosition: Position = Position(),
    val quaternion: Quaternion = Quaternion(),
    val scale: Scale = Scale(),
    val startMarkerId: String? = null,
    val endMarkerId: String? = null,
    val length: Float = 0f,
) {
    constructor(
        startPos: Position,
        endPos: Position,
        camPos: Position,
        startMarkerId: String? = null,
        endMarkerId: String? = null
    ) : this(
        worldPosition = (startPos + endPos) / 2f,
        quaternion = calculateQuaternion(startPos, endPos, camPos),
        scale = Float3(length(endPos - startPos), 1f, 1f),
        startMarkerId = startMarkerId,
        endMarkerId = endMarkerId,
        length = length(endPos - startPos)
    )

    fun copy(startPos: Position, endPos: Position, camPos: Position): SegmentNodeState = this.copy(
        worldPosition = (startPos + endPos) / 2f,
        quaternion = calculateQuaternion(startPos, endPos, camPos),
        scale = Float3(length(endPos - startPos), 1f, 1f),
        length = length(endPos - startPos)
    )

    companion object {
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
    }
}
