package dev.maruffirdaus.geopocket.ui.ar.node.util

import com.google.android.filament.Engine
import com.google.android.filament.MaterialInstance
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Float4
import dev.romainguy.kotlin.math.Mat4
import dev.romainguy.kotlin.math.Quaternion
import dev.romainguy.kotlin.math.cross
import dev.romainguy.kotlin.math.length
import dev.romainguy.kotlin.math.normalize
import io.github.sceneview.math.Position
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.Node

object LineNodeUtil {
    fun create(
        engine: Engine,
        materialInstance: MaterialInstance,
        startPos: Position,
        endPos: Position,
        camPos: Position
    ): Node = CubeNode(engine = engine, materialInstance = materialInstance).apply {
        collisionShape = null
        isPositionEditable = false
        update(this@apply, startPos, endPos, camPos)
    }

    fun update(node: Node, startPos: Position, endPos: Position, camPos: Position) {
        node.apply {
            worldPosition = (startPos + endPos) / 2f
            quaternion = calculateQuaternion(startPos, endPos, camPos)
            scale = Float3(length(endPos - startPos), 0.0025f, 0.0001f)
        }
    }

    private fun calculateQuaternion(startPos: Position, endPos: Position, camPos: Position): Quaternion {
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