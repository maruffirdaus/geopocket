package dev.maruffirdaus.geopocket.ui.ar.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.filament.Engine
import com.google.ar.core.Config
import com.google.ar.core.Frame
import dev.maruffirdaus.geopocket.ui.ar.node.NodeManager
import dev.romainguy.kotlin.math.Float2
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.node.ViewNode2
import io.github.sceneview.rememberView

@Composable
fun CustomArScene(
    engine: Engine,
    materialLoader: MaterialLoader,
    windowManager: ViewNode2.WindowManager,
    nodeManager: NodeManager
) {
    val state by nodeManager.state.collectAsStateWithLifecycle()

    var arSceneWidth by remember { mutableIntStateOf(0) }
    var arSceneHeight by remember { mutableIntStateOf(0) }

    val view = rememberView(engine).apply {
        setShadowingEnabled(false)
    }

    val cameraNode = rememberARCameraNode(engine)

    var frame by remember { mutableStateOf<Frame?>(null) }

    ARScene(
        modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
            arSceneWidth = layoutCoordinates.size.width
            arSceneHeight = layoutCoordinates.size.height
        },
        engine = engine,
        materialLoader = materialLoader,
        sessionConfiguration = { session, config ->
            config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
            config.depthMode =
                when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                    true -> Config.DepthMode.AUTOMATIC
                    else -> Config.DepthMode.DISABLED
                }
            config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
            config.lightEstimationMode = Config.LightEstimationMode.DISABLED
        },
        planeRenderer = false,
        view = view,
        cameraNode = cameraNode,
        childNodes = buildList {
            state.crosshairNode?.let { node ->
                add(node)
            }
            state.lineHelperNode?.let { node ->
                add(node)
            }
            addAll(state.markerNodes)
            addAll(state.lineNodes)
            addAll(state.lineLabelNodes)
        },
        viewNodeWindowManager = windowManager,
        onSessionUpdated = { _, updatedFrame ->
            frame = updatedFrame

            val centerPoint = Float2(
                x = arSceneWidth / 2f,
                y = arSceneHeight / 2f
            )

            val hitResults = frame?.hitTest(centerPoint.x, centerPoint.y)
            val hitResult = hitResults?.firstOrNull {
                it.isValid(
                    depthPoint = false,
                    point = false
                )
            }

            if (hitResult != null) {
                nodeManager.updateCrosshairNode(
                    hitResult.trackable,
                    hitResult.hitPose,
                    cameraNode.worldPosition
                )
            }
        }
    )
}