package dev.maruffirdaus.geopocket.ui.ar.component

import androidx.compose.runtime.Composable
import dev.maruffirdaus.geopocket.ui.ar.model.AngleNodeState
import io.github.sceneview.SceneScope
import io.github.sceneview.math.toRotation
import io.github.sceneview.node.ViewNode

/**
 * WARNING: Contains ViewNode. Keep alive and toggle [visible] instead of conditional composition.
 */
@Composable
fun SceneScope.AngleNode(
    state: AngleNodeState,
    windowManager: ViewNode.WindowManager,
    visible: Boolean
) {
    ViewNodeWrapper(
        windowManager = windowManager,
        position = state.worldPosition,
        rotation = state.quaternion.toRotation(),
        visible = visible
    ) {
        Label("${state.degree.toInt()}°")
    }
}