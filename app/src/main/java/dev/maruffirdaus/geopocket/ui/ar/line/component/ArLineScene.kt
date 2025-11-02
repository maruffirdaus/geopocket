package dev.maruffirdaus.geopocket.ui.ar.line.component

import android.widget.Toast
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.ar.core.Config
import com.google.ar.core.Frame
import dev.maruffirdaus.geopocket.ui.ar.common.util.NodeUtil
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberViewNodeManager
import java.util.Locale

@Composable
fun ArLineScene() {
    val context = LocalContext.current

    val engine = rememberEngine()
    val materialLoader = rememberMaterialLoader(engine)
    val markerNodes = remember { mutableStateListOf<AnchorNode>() }
    val lineNodes = rememberNodes()
    var frame by remember { mutableStateOf<Frame?>(null) }
    val windowManager = rememberViewNodeManager()

    LaunchedEffect(markerNodes.size) {
        if (markerNodes.size == 2) {
            lineNodes += NodeUtil.createLineBetweenNodes(
                engine,
                materialLoader,
                windowManager,
                markerNodes[0],
                markerNodes[1]
            )

            val distance = NodeUtil.calculateDistance(markerNodes[0], markerNodes[1])

            Toast.makeText(
                context,
                "Distance: ${String.format(Locale.getDefault(), "%.2f", distance)} meters",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

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
            config.lightEstimationMode =
                Config.LightEstimationMode.ENVIRONMENTAL_HDR
        },
        childNodes = markerNodes + lineNodes,
        viewNodeWindowManager = windowManager,
        onSessionUpdated = { _, updatedFrame ->
            frame = updatedFrame
        },
        onGestureListener = rememberOnGestureListener(
            onSingleTapConfirmed = { motionEvent, node ->
                if (markerNodes.size < 2) {
                    if (node == null) {
                        val hitResults = frame?.hitTest(motionEvent.x, motionEvent.y)
                        hitResults?.firstOrNull {
                            it.isValid(
                                depthPoint = false,
                                point = false
                            )
                        }?.createAnchorOrNull()
                            ?.let { anchor ->
                                markerNodes += NodeUtil.createMarkerNode(
                                    engine = engine,
                                    windowManager = windowManager,
                                    materialLoader = materialLoader,
                                    anchor = anchor,
                                    label = ('A' + markerNodes.size).toString()
                                )
                            }
                    }
                } else {
                    Toast.makeText(context, "You can only add 2 nodes", Toast.LENGTH_SHORT).show()
                }
            }
        )
    )
}