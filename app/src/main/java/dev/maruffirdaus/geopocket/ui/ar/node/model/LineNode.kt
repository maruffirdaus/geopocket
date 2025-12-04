package dev.maruffirdaus.geopocket.ui.ar.node.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.android.filament.Engine
import com.google.android.filament.Material
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Float4
import dev.romainguy.kotlin.math.Mat4
import dev.romainguy.kotlin.math.Quaternion
import dev.romainguy.kotlin.math.cross
import dev.romainguy.kotlin.math.dot
import dev.romainguy.kotlin.math.length
import dev.romainguy.kotlin.math.normalize
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.math.Position
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.ViewNode2
import java.util.UUID

class LineNode(
    engine: Engine,
    windowManager: ViewNode2.WindowManager,
    materialLoader: MaterialLoader,
    material: Material,
    startPos: Position,
    endPos: Position,
    camPos: Position,
    labelContent: @Composable LineNode.() -> Unit,
    val id: String = UUID.randomUUID().toString(),
    val startNodeId: String? = null,
    val endNodeId: String? = null
) {
    val node = CubeNode(
        engine = engine,
        materialInstance = materialLoader.createInstance(material)
    ).apply {
        collisionShape = null
        isPositionEditable = false
    }
    val labelNode = ViewNode2(
        engine = engine,
        windowManager = windowManager,
        materialLoader = materialLoader,
        unlit = true,
        content = { labelContent() }
    ).apply {
        pxPerUnits = 2000f
        collisionShape = null
        isPositionEditable = false
    }
    var length by mutableStateOf(0f)
        private set

    init {
        update(startPos, endPos, camPos)
    }

    fun update(startPos: Position, endPos: Position, camPos: Position) {
        updateLine(startPos, endPos, camPos)
        updateLabel(startPos, endPos, camPos)
        length = length(endPos - startPos)
    }

    private fun updateLine(startPos: Position, endPos: Position, camPos: Position) {
        node.apply {
            worldPosition = (startPos + endPos) / 2f
            quaternion = calculateLineQuaternion(startPos, endPos, camPos)
            scale = Float3(length(endPos - startPos), 0.0025f, 0.0001f)
        }
    }

    private fun calculateLineQuaternion(
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

    private fun updateLabel(startPos: Position, endPos: Position, camPos: Position) {
        labelNode.apply {
            val quaternion = calculateLabelQuaternion(startPos, endPos, camPos)

            val localUp = quaternion * Float3(0f, 0f, 1f)
            val offsetDistance = 0.0001f

            val midPoint = (startPos + endPos) / 2f

            worldPosition = midPoint + localUp * offsetDistance
            this.quaternion = quaternion
        }
    }

    private fun calculateLabelQuaternion(
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

    fun destroy() {
        node.destroy()
        labelNode.destroy()
    }
}