package dev.maruffirdaus.geopocket.ui.ar.node.util

import androidx.compose.runtime.Composable
import com.google.android.filament.Engine
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Float4
import dev.romainguy.kotlin.math.Mat4
import dev.romainguy.kotlin.math.Quaternion
import dev.romainguy.kotlin.math.cross
import dev.romainguy.kotlin.math.dot
import dev.romainguy.kotlin.math.normalize
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.math.Position
import io.github.sceneview.node.ViewNode2

object LineLabelNodeUtil {
    fun create(
        engine: Engine,
        windowManager: ViewNode2.WindowManager,
        materialLoader: MaterialLoader,
        startPos: Position,
        endPos: Position,
        camPos: Position,
        content: @Composable () -> Unit
    ): ViewNode2 = ViewNode2(
        engine = engine,
        windowManager = windowManager,
        materialLoader = materialLoader,
        unlit = true,
        content = content
    ).apply {
        pxPerUnits = 2000f
        collisionShape = null
        isPositionEditable = false
        update(this@apply, startPos, endPos, camPos)
    }

    fun update(
        node: ViewNode2,
        startPos: Position,
        endPos: Position,
        camPos: Position
    ) {
        node.apply {
            val quaternion = calculateQuaternion(startPos, endPos, camPos)

            val localUp = quaternion * Float3(0f, 0f, 1f)
            val offsetDistance = 0.0001f

            val midPoint = (startPos + endPos) / 2f

            worldPosition = midPoint + localUp * offsetDistance
            this.quaternion = quaternion
        }
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