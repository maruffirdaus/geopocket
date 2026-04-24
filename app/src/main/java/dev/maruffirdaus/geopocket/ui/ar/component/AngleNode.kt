package dev.maruffirdaus.geopocket.ui.ar.component

import androidx.compose.runtime.Composable
import dev.maruffirdaus.geopocket.ui.ar.model.AngleNodeState
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.SceneScope
import io.github.sceneview.math.toRotation
import io.github.sceneview.node.ViewNode

@Composable
fun SceneScope.AngleNode(
    state: AngleNodeState,
    windowManager: ViewNode.WindowManager
) {
    val correction = Quaternion.fromAxisAngle(Float3(1f, 0f, 0f), -90f)

    ViewNodeWrapper(
        windowManager = windowManager,
        position = state.worldPosition,
        rotation = (state.quaternion * correction).toRotation()
    ) {
        Label("${state.degree.toInt()}°")
    }
}