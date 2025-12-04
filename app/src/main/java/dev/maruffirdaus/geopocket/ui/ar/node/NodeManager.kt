package dev.maruffirdaus.geopocket.ui.ar.node

import androidx.compose.runtime.Composable
import com.google.android.filament.Engine
import com.google.android.filament.Material
import com.google.ar.core.Pose
import com.google.ar.core.Trackable
import dev.maruffirdaus.geopocket.ui.ar.node.model.CrosshairNode
import dev.maruffirdaus.geopocket.ui.ar.node.model.LineNode
import dev.maruffirdaus.geopocket.ui.ar.node.model.MarkerNode
import dev.maruffirdaus.geopocket.ui.common.model.ArPlacingMode
import io.github.sceneview.ar.arcore.position
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.math.Position
import io.github.sceneview.node.ViewNode2
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NodeManager(
    private val engine: Engine,
    private val modelLoader: ModelLoader,
    private val materialLoader: MaterialLoader,
    private val windowManager: ViewNode2.WindowManager,
    private val mode: ArPlacingMode,
    private val crosshairContent: @Composable () -> Unit,
    private val lineLabelContent: @Composable (Float) -> Unit
) {
    private val _state = MutableStateFlow(NodeManagerState())
    val state = _state.asStateFlow()

    private var isLineHelperNodeEnabled = true

    private var currentTrackable: Trackable? = null
    private var currentPose: Pose? = null
    private var currentCamPos: Position? = null

    private var unlitMaterial: Material? = null

    suspend fun loadMaterial() {
        unlitMaterial = materialLoader.loadMaterial("materials/unlit_material.filamat")?.apply {
            setDefaultParameter("color", 1f, 1f, 1f, 1f)
        }
    }

    fun addMarkerNode(onMaxNodesReached: () -> Unit) {
        if (state.value.markerNodes.size == mode.maxNodes) {
            onMaxNodesReached()
            return
        }

        val anchor = currentTrackable?.createAnchor(currentPose ?: return) ?: return
        val markerNode = MarkerNode(
            engine = engine,
            materialInstance = materialLoader.createInstance(unlitMaterial ?: return),
            anchor = anchor,
            onPoseChange = { pose ->
                connectedNodeIds.forEach { connectedNodeId ->
                    val lineNode = state.value.lineNodes[connectedNodeId] ?: return@forEach
                    val isStartNode = id == lineNode.startNodeId
                    val startPos = if (isStartNode) {
                        pose.position
                    } else {
                        state.value.markerNodes[lineNode.startNodeId]?.node?.worldPosition
                            ?: return@forEach
                    }
                    val endPos = if (isStartNode) {
                        state.value.markerNodes[lineNode.endNodeId]?.node?.worldPosition
                            ?: return@forEach
                    } else {
                        pose.position
                    }
                    lineNode.update(startPos, endPos, currentCamPos ?: return@forEach)
                }
            }
        )

        if (state.value.markerNodes.isNotEmpty()) {
            addLineNode(state.value.markerNodes.values.last(), markerNode)
        }

        _state.update {
            it.copy(markerNodes = it.markerNodes + mapOf(markerNode.id to markerNode))
        }

        if (state.value.markerNodes.size == mode.maxNodes) {
            disableLineHelperNode()

            if (state.value.markerNodes.size > 2) {
                addLineNode(markerNode, state.value.markerNodes.values.first())
            }
        }
    }

    private fun addLineNode(startNode: MarkerNode, endNode: MarkerNode) {
        val lineNode = LineNode(
            engine = engine,
            windowManager = windowManager,
            materialLoader = materialLoader,
            material = unlitMaterial ?: return,
            startPos = startNode.node.worldPosition,
            endPos = endNode.node.worldPosition,
            camPos = currentCamPos ?: return,
            labelContent = { lineLabelContent(length) },
            startNodeId = startNode.id,
            endNodeId = endNode.id
        )

        startNode.connectedNodeIds += lineNode.id
        endNode.connectedNodeIds += lineNode.id

        _state.update {
            it.copy(lineNodes = it.lineNodes + mapOf(lineNode.id to lineNode))
        }
    }

    fun updateCrosshairNode(trackable: Trackable, pose: Pose, camPos: Position) {
        if (state.value.crosshairNode == null) {
            _state.update {
                it.copy(
                    crosshairNode = CrosshairNode(
                        engine = engine,
                        windowManager = windowManager,
                        materialLoader = materialLoader,
                        content = crosshairContent
                    )
                )
            }
        }

        state.value.crosshairNode?.update(pose)

        currentTrackable = trackable
        currentPose = state.value.crosshairNode?.calculateCorrectedPose()
        currentCamPos = camPos

        state.value.lineHelperNode?.update(
            startPos = state.value.markerNodes.values.lastOrNull()?.node?.worldPosition ?: return,
            endPos = state.value.crosshairNode?.node?.worldPosition ?: return,
            camPos = currentCamPos ?: return
        )

        if (isLineHelperNodeEnabled && state.value.lineHelperNode == null) {
            addLineHelperNode()
        }
    }

    private fun addLineHelperNode() {
        _state.update {
            it.copy(
                lineHelperNode = LineNode(
                    engine = engine,
                    windowManager = windowManager,
                    materialLoader = materialLoader,
                    material = unlitMaterial ?: return,
                    startPos = state.value.markerNodes.values.lastOrNull()?.node?.worldPosition
                        ?: return,
                    endPos = state.value.crosshairNode?.node?.worldPosition ?: return,
                    camPos = currentCamPos ?: return,
                    labelContent = { lineLabelContent(length) }
                )
            )
        }
    }

    fun enableLineHelperNode() {
        isLineHelperNodeEnabled = true
    }

    fun disableLineHelperNode() {
        state.value.lineHelperNode?.destroy()
        _state.update {
            it.copy(lineHelperNode = null)
        }
        isLineHelperNodeEnabled = false
    }

    fun clearNodes() {
        state.value.markerNodes.values.forEach {
            it.destroy()
        }
        state.value.lineNodes.values.forEach {
            it.destroy()
        }
        _state.update {
            it.copy(
                lineHelperNode = null,
                markerNodes = mapOf(),
                lineNodes = mapOf()
            )
        }
        enableLineHelperNode()
    }
}