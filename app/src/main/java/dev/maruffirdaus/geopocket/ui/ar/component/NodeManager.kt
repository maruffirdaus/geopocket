package dev.maruffirdaus.geopocket.ui.ar.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.filament.Engine
import com.google.android.filament.MaterialInstance
import com.google.ar.core.Anchor
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Float4
import dev.romainguy.kotlin.math.Mat4
import dev.romainguy.kotlin.math.Quaternion
import dev.romainguy.kotlin.math.cross
import dev.romainguy.kotlin.math.dot
import dev.romainguy.kotlin.math.length
import dev.romainguy.kotlin.math.normalize
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.collision.Box
import io.github.sceneview.collision.Vector3
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.material.setColor
import io.github.sceneview.material.setMetallic
import io.github.sceneview.material.setReflectance
import io.github.sceneview.material.setRoughness
import io.github.sceneview.math.Position
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.CylinderNode
import io.github.sceneview.node.Node
import io.github.sceneview.node.ViewNode2
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NodeManager(
    private val engine: Engine,
    private val materialLoader: MaterialLoader,
    private val windowManager: ViewNode2.WindowManager,
    private val lineLabelContent: @Composable (Float) -> Unit
) {
    private val _markerNodes = MutableStateFlow(listOf<AnchorNode>())
    val markerNodes = _markerNodes.asStateFlow()

    private val _lineNodes = MutableStateFlow(listOf<Node>())
    val lineNodes = _lineNodes.asStateFlow()

    private val _lineLabelNodes = MutableStateFlow(listOf<ViewNode2>())
    val lineLabelNodes = _lineLabelNodes.asStateFlow()

    private val lineLength = MutableStateFlow(listOf<Float>())

    private val flatMaterial = materialLoader.createColorInstance(Color.Black).apply {
        setColor(Float4(5.0f, 5.0f, 5.0f, 1.0f))
        setRoughness(1f)
        setMetallic(0f)
        setReflectance(0f)
    }

    fun createMarkerNode(anchor: Anchor, camPos: Position) {
        val currentIndex = markerNodes.value.size
        val previousIndex = currentIndex - 1
        val nextIndex = currentIndex + 1

        val anchorNode = AnchorNode(
            engine = engine,
            anchor = anchor,
            onPoseChanged = { pose ->
                val position = Position(pose.tx(), pose.ty(), pose.tz())
                markerNodes.value.getOrNull(previousIndex)?.let { previousNode ->
                    lineLength.update {
                        it.mapIndexed { index, item ->
                            if (index == previousIndex) {
                                length(position - previousNode.worldPosition)
                            } else {
                                item
                            }
                        }
                    }
                    updateLineNode(
                        lineNodes.value[previousIndex],
                        previousNode.worldPosition,
                        position,
                        camPos
                    )
                    updateLineLabelNode(
                        lineLabelNodes.value[previousIndex],
                        previousNode.worldPosition,
                        position,
                        camPos
                    )
                }
                markerNodes.value.getOrNull(nextIndex)?.let { nextNode ->
                    lineLength.update {
                        it.mapIndexed { index, item ->
                            if (index == currentIndex) {
                                length(position - nextNode.worldPosition)
                            } else {
                                item
                            }
                        }
                    }
                    updateLineNode(
                        lineNodes.value[currentIndex],
                        position,
                        nextNode.worldPosition,
                        camPos
                    )
                    updateLineLabelNode(
                        lineLabelNodes.value[currentIndex],
                        position,
                        nextNode.worldPosition,
                        camPos
                    )
                }
            }
        ).apply {
            val cylinderNode = CylinderNode(
                engine = engine,
                radius = 0.005f,
                height = 0.0001f,
                materialInstance = flatMaterial
            )
            collisionShape = Box(Vector3(0.1f, 0.1f, 0.1f))
            addChildNode(cylinderNode)
        }

        _markerNodes.update {
            it + anchorNode
        }

        if (markerNodes.value.size > 1) {
            val startPos = markerNodes.value[previousIndex].worldPosition
            val endPos = markerNodes.value[currentIndex].worldPosition
            lineLength.update {
                it + length(endPos - startPos)
            }
            createLineNode(startPos, endPos, camPos, flatMaterial)
            createLineLabelNode(previousIndex, startPos, endPos, camPos)
        }
    }

    private fun createLineNode(
        startPos: Position,
        endPos: Position,
        camPos: Position,
        materialInstance: MaterialInstance
    ) {
        _lineNodes.update {
            it + CubeNode(engine = engine, materialInstance = materialInstance).apply {
                collisionShape = null
                updateLineNode(this@apply, startPos, endPos, camPos)
            }
        }
    }

    private fun updateLineNode(node: Node, startPos: Position, endPos: Position, camPos: Position) {
        node.apply {
            worldPosition = (startPos + endPos) / 2f
            quaternion = calculateLineNodeQuaternion(startPos, endPos, camPos)
            scale = Float3(length(endPos - startPos), 0.0025f, 0.0001f)
        }
    }

    private fun calculateLineNodeQuaternion(
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

    private fun createLineLabelNode(
        index: Int,
        startPos: Position,
        endPos: Position,
        camPos: Position
    ) {
        _lineLabelNodes.update {
            it + ViewNode2(
                engine = engine,
                windowManager = windowManager,
                materialLoader = materialLoader,
                unlit = true,
                content = { lineLabelContent(lineLength.collectAsStateWithLifecycle().value[index]) }
            ).apply {
                pxPerUnits = 2000f
                collisionShape = null
                isPositionEditable = false
                updateGeometrySize()
                updateLineLabelNode(this@apply, startPos, endPos, camPos)
            }
        }
    }

    private fun updateLineLabelNode(
        node: ViewNode2,
        startPos: Position,
        endPos: Position,
        camPos: Position
    ) {
        node.apply {
            val quaternion = calculateLineLabelNodeQuaternion(startPos, endPos, camPos)

            val localUp = quaternion * Float3(0f, 0f, 1f)
            val offsetDistance = 0.0001f

            val midPoint = (startPos + endPos) / 2f

            worldPosition = midPoint + localUp * offsetDistance
            this.quaternion = quaternion
        }
    }

    private fun calculateLineLabelNodeQuaternion(
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

    fun clearNodes() {
        _markerNodes.update {
            emptyList()
        }
        _lineNodes.update {
            emptyList()
        }
        _lineLabelNodes.update {
            emptyList()
        }
    }

    fun destroy() {
        markerNodes.value.forEach {
            it.destroy()
        }
        lineNodes.value.forEach {
            it.destroy()
        }
        lineLabelNodes.value.forEach {
            it.destroy()
        }
        windowManager.destroy()
        materialLoader.destroy()
        engine.destroy()
    }
}