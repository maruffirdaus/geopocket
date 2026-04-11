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
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
data class LineLabelNode(
    val worldPosition: Position = Position(),
    val quaternion: Quaternion = Quaternion(),
    val length: Float = 0f
) {
    constructor(startPos: Position, endPos: Position, camPos: Position) : this(
        worldPosition = calculateWorldPosition(
            calculateQuaternion(startPos, endPos, camPos),
            startPos,
            endPos
        ),
        quaternion = calculateQuaternion(startPos, endPos, camPos),
        length = length(endPos - startPos)
    )

    companion object {
        private fun calculateWorldPosition(
            quaternion: Quaternion,
            startPos: Position,
            endPos: Position
        ): Position {
            val localUp = quaternion * Float3(0f, 0f, 1f)
            val offsetDistance = 0.0001f

            val midPoint = (startPos + endPos) / 2f

            return midPoint + localUp * offsetDistance
        }

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
