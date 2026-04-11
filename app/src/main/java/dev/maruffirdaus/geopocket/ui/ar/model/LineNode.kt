package dev.maruffirdaus.geopocket.ui.ar.model

import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Float4
import dev.romainguy.kotlin.math.Mat4
import dev.romainguy.kotlin.math.Quaternion
import dev.romainguy.kotlin.math.cross
import dev.romainguy.kotlin.math.length
import dev.romainguy.kotlin.math.normalize
import io.github.sceneview.math.Position
import io.github.sceneview.math.Scale
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class LineNode(
    val id: String = Uuid.random().toString(),
    val worldPosition: Position = Position(),
    val quaternion: Quaternion = Quaternion(),
    val scale: Scale = Scale(),
    val startMarkerId: String? = null,
    val endMarkerId: String? = null,
    val label: LineLabelNode = LineLabelNode(),
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
        scale = Float3(length(endPos - startPos), 0.0025f, 0.0001f),
        startMarkerId = startMarkerId,
        endMarkerId = endMarkerId,
        label = LineLabelNode(startPos, endPos, camPos)
    )

    fun copy(startPos: Position, endPos: Position, camPos: Position): LineNode = this.copy(
        worldPosition = (startPos + endPos) / 2f,
        quaternion = calculateQuaternion(startPos, endPos, camPos),
        scale = Float3(length(endPos - startPos), 0.0025f, 0.0001f),
        label = LineLabelNode(startPos, endPos, camPos)
    )

    companion object {
        private fun calculateQuaternion(
            startPos: Position,
            endPos: Position,
            camPos: Position
        ): Quaternion {
            val midPoint = (startPos + endPos) / 2f

            var xAxis = endPos - startPos
            if (length(xAxis) < 0.0001f) return Quaternion()
            xAxis = normalize(xAxis)

            var zAxis = camPos - midPoint

            var yAxis = cross(xAxis, zAxis)

            yAxis = if (length(yAxis) < 0.0001f) {
                Float3(0f, 1f, 0f)
            } else {
                normalize(yAxis)
            }

            zAxis = cross(xAxis, yAxis)
            zAxis = normalize(zAxis)

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
