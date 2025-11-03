package dev.maruffirdaus.geopocket.ui.ar.line.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.android.filament.Engine
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Frame
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.node.Node
import io.github.sceneview.node.ViewNode2
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberViewNodeManager

@Composable
fun ArLineScene(
    markerNodes: List<AnchorNode>,
    lineNodes: List<Node>,
    onMarkerNodeCreate: (engine: Engine, materialLoader: MaterialLoader, windowManager: ViewNode2.WindowManager, anchor: Anchor) -> Unit
) {
    val engine = rememberEngine()
    val materialLoader = rememberMaterialLoader(engine)
    var frame by remember { mutableStateOf<Frame?>(null) }
    val windowManager = rememberViewNodeManager()

    ARScene(
        engine = engine,
        materialLoader = materialLoader,
        sessionConfiguration = { session, config ->
            config.depthMode =
                when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                    true -> Config.DepthMode.AUTOMATIC
                    else -> Config.DepthMode.DISABLED
                }
            config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
            config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
        },
        childNodes = markerNodes + lineNodes,
        viewNodeWindowManager = windowManager,
        onSessionUpdated = { _, updatedFrame ->
            frame = updatedFrame
        },
        onGestureListener = rememberOnGestureListener(
            onSingleTapConfirmed = { motionEvent, _ ->
                val hitResults = frame?.hitTest(motionEvent.x, motionEvent.y)
                hitResults?.firstOrNull {
                    it.isValid(
                        depthPoint = false,
                        point = false
                    )
                }?.createAnchorOrNull()
                    ?.let { anchor ->
                        onMarkerNodeCreate(engine, materialLoader, windowManager, anchor)
                    }
            }
        )
    )
}