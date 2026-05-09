package dev.maruffirdaus.geopocket.ui.ar.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import dev.maruffirdaus.geopocket.ui.ar.model.SegmentNodeState
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.SceneScope
import io.github.sceneview.math.toRotation
import io.github.sceneview.node.ViewNode

/**
 * WARNING: Contains ViewNode. Keep alive and toggle [visible] instead of conditional composition.
 */
@Composable
fun SceneScope.SegmentNode(
    state: SegmentNodeState,
    windowManager: ViewNode.WindowManager,
    visible: Boolean
) {
    val localUp = state.quaternion * Float3(0f, 0f, 1f)

    val blackMaterial = remember(materialLoader) {
        materialLoader.createUnlitColorInstance(Color.Black)
    }

    val cubeWidth = 0.0025f
    val cubeHeight = 0.0001f

    if (visible) CubeNode(
        size = Float3(1f, cubeWidth, cubeHeight),
        materialInstance = blackMaterial,
        position = state.worldPosition,
        rotation = state.quaternion.toRotation(),
        scale = state.scale
    )
    ViewNodeWrapper(
        windowManager = windowManager,
        position = state.worldPosition + localUp * cubeHeight,
        rotation = state.quaternion.toRotation(),
        visible = visible
    ) {
        Label("${(state.length * 100).toInt()} cm")
    }
}