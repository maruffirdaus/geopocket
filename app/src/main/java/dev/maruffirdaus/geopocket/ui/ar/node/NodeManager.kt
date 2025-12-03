package dev.maruffirdaus.geopocket.ui.ar.node

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.filament.Engine
import com.google.ar.core.Pose
import com.google.ar.core.Trackable
import dev.maruffirdaus.geopocket.ui.ar.node.util.CrosshairNodeUtil
import dev.maruffirdaus.geopocket.ui.ar.node.util.LineLabelNodeUtil
import dev.maruffirdaus.geopocket.ui.ar.node.util.LineNodeUtil
import dev.maruffirdaus.geopocket.ui.ar.node.util.MarkerNodeUtil
import dev.maruffirdaus.geopocket.ui.common.model.ArPlacingMode
import dev.romainguy.kotlin.math.Float4
import dev.romainguy.kotlin.math.length
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.math.Position
import io.github.sceneview.node.ViewNode2
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NodeManager(
    private val engine: Engine,
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

    private val lineLengths = MutableStateFlow(listOf<Float>())

    private val flatMaterial = materialLoader.createColorInstance(
        color = Float4(5.0f, 5.0f, 5.0f, 1.0f),
        metallic = 0f,
        roughness = 1f,
        reflectance = 0f
    )

    fun addMarkerNode(onMaxNodesReached: () -> Unit) {
        if (state.value.markerNodes.size == mode.maxNodes) {
            onMaxNodesReached()
            return
        }

        val anchor = currentTrackable?.createAnchor(currentPose ?: return) ?: return

        val currentIndex = state.value.markerNodes.size
        val previousIndex = currentIndex - 1
        val nextIndex = (currentIndex + 1).takeIf { it < mode.maxNodes } ?: 0

        val markerNode = MarkerNodeUtil.create(
            engine = engine,
            materialInstance = flatMaterial,
            anchor = anchor
        ) { pose ->
            val updatedPreviousIndex =
                if (previousIndex == -1 &&
                    state.value.markerNodes.size == mode.maxNodes &&
                    state.value.lineNodes.size > 1
                ) {
                    state.value.markerNodes.lastIndex
                } else {
                    previousIndex
                }
            val position = Position(pose.tx(), pose.ty(), pose.tz())

            state.value.markerNodes.getOrNull(updatedPreviousIndex)?.let { previousNode ->
                lineLengths.update {
                    it.mapIndexed { index, item ->
                        if (index == previousIndex) {
                            length(position - previousNode.worldPosition)
                        } else {
                            item
                        }
                    }
                }
                LineNodeUtil.update(
                    state.value.lineNodes.getOrNull(updatedPreviousIndex) ?: return@let,
                    previousNode.worldPosition,
                    position,
                    currentCamPos ?: return@let
                )
                LineLabelNodeUtil.update(
                    state.value.lineLabelNodes.getOrNull(updatedPreviousIndex) ?: return@let,
                    previousNode.worldPosition,
                    position,
                    currentCamPos ?: return@let
                )
            }
            state.value.markerNodes.getOrNull(nextIndex)?.let { nextNode ->
                lineLengths.update {
                    it.mapIndexed { index, item ->
                        if (index == currentIndex) {
                            length(position - nextNode.worldPosition)
                        } else {
                            item
                        }
                    }
                }
                LineNodeUtil.update(
                    state.value.lineNodes.getOrNull(currentIndex) ?: return@let,
                    position,
                    nextNode.worldPosition,
                    currentCamPos ?: return@let
                )
                LineLabelNodeUtil.update(
                    state.value.lineLabelNodes.getOrNull(currentIndex) ?: return@let,
                    position,
                    nextNode.worldPosition,
                    currentCamPos ?: return@let
                )
            }
        }

        _state.update {
            it.copy(markerNodes = it.markerNodes + markerNode)
        }

        if (state.value.markerNodes.size > 1) {
            addLineNode(previousIndex, currentIndex)
            addLineLabelNode(previousIndex, currentIndex)
        }

        if (state.value.markerNodes.size == mode.maxNodes) {
            disableLineHelperNode()

            if (state.value.markerNodes.size > 2) {
                addLineNode(currentIndex, nextIndex)
                addLineLabelNode(currentIndex, nextIndex)
            }
        }
    }

    private fun addLineNode(startNodeIndex: Int, endNodeIndex: Int) {
        val startPos = state.value.markerNodes.getOrNull(startNodeIndex)?.worldPosition ?: return
        val endPos = state.value.markerNodes.getOrNull(endNodeIndex)?.worldPosition ?: return

        val lineNode = LineNodeUtil.create(
            engine = engine,
            materialInstance = flatMaterial,
            startPos = startPos,
            endPos = endPos,
            camPos = currentCamPos ?: return
        )

        _state.update {
            it.copy(lineNodes = it.lineNodes + lineNode)
        }
    }

    private fun addLineLabelNode(startNodeIndex: Int, endNodeIndex: Int) {
        val startPos = state.value.markerNodes.getOrNull(startNodeIndex)?.worldPosition ?: return
        val endPos = state.value.markerNodes.getOrNull(endNodeIndex)?.worldPosition ?: return

        lineLengths.update {
            it + length(endPos - startPos)
        }

        val lineLabelNode = LineLabelNodeUtil.create(
            engine = engine,
            windowManager = windowManager,
            materialLoader = materialLoader,
            startPos = startPos,
            endPos = endPos,
            camPos = currentCamPos ?: return,
            content = {
                lineLabelContent(lineLengths.collectAsStateWithLifecycle().value[startNodeIndex])
            }
        )

        _state.update {
            it.copy(lineLabelNodes = it.lineLabelNodes + lineLabelNode)
        }
    }

    fun updateCrosshairNode(trackable: Trackable, pose: Pose, camPos: Position) {
        if (state.value.crosshairNode == null) {
            _state.update {
                it.copy(
                    crosshairNode = CrosshairNodeUtil.create(
                        engine = engine,
                        windowManager = windowManager,
                        materialLoader = materialLoader,
                        content = crosshairContent
                    )
                )
            }
        }

        state.value.crosshairNode?.let {
            CrosshairNodeUtil.update(it, pose)
        }

        currentTrackable = trackable
        currentPose = CrosshairNodeUtil.calculateCorrectedPose(pose)
        currentCamPos = camPos

        state.value.lineHelperNode?.let { node ->
            LineNodeUtil.update(
                node = node,
                startPos = state.value.markerNodes.lastOrNull()?.worldPosition ?: return@let,
                endPos = state.value.crosshairNode?.worldPosition ?: return@let,
                camPos = currentCamPos ?: return@let
            )
        }

        if (isLineHelperNodeEnabled && state.value.lineHelperNode == null) {
            addLineHelperNode()
        }
    }

    private fun addLineHelperNode() {
        _state.update {
            it.copy(
                lineHelperNode = LineNodeUtil.create(
                    engine = engine,
                    materialInstance = flatMaterial,
                    startPos = state.value.markerNodes.lastOrNull()?.worldPosition ?: return,
                    endPos = state.value.crosshairNode?.worldPosition ?: return,
                    camPos = currentCamPos ?: return
                )
            )
        }
    }

    fun enableLineHelperNode() {
        isLineHelperNodeEnabled = true
    }

    fun disableLineHelperNode() {
        _state.update {
            it.copy(lineHelperNode = null)
        }
        isLineHelperNodeEnabled = false
    }

    fun clearNodes() {
        _state.update {
            it.copy(
                lineHelperNode = null,
                markerNodes = listOf(),
                lineNodes = listOf(),
                lineLabelNodes = listOf()
            )
        }
        enableLineHelperNode()
    }
}