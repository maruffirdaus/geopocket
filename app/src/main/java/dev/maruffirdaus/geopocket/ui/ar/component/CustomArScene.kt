package dev.maruffirdaus.geopocket.ui.ar.component

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import com.google.android.filament.Engine
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Pose
import com.google.ar.core.Trackable
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme
import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.ar.arcore.position
import io.github.sceneview.ar.arcore.quaternion
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.math.Position
import io.github.sceneview.node.Node
import io.github.sceneview.node.ViewNode2
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberNode
import io.github.sceneview.rememberView
import io.github.sceneview.rememberViewNodeManager

@Composable
fun CustomArScene(
    markerNodes: List<Node>,
    lineNodes: List<Node>,
    lineLabelNodes: List<Node>,
    onCrosshairMove: (Trackable, Pose, Position) -> Unit,
    engine: Engine = rememberEngine(),
    materialLoader: MaterialLoader = rememberMaterialLoader(engine),
    windowManager: ViewNode2.WindowManager = rememberViewNodeManager()
) {
    val view = rememberView(engine).apply {
        setShadowingEnabled(false)
    }

    val cameraNode = rememberARCameraNode(engine)
    val crosshairNode = rememberNode {
        ViewNode2(
            engine = engine,
            windowManager = windowManager,
            materialLoader = materialLoader,
            unlit = true,
            content = {
                GeoPocketTheme {
                    Box(contentAlignment = Alignment.Center) {
                        Crosshair()
                    }
                }
            }
        ).apply {
            pxPerUnits = 2000f
            collisionShape = null
            isPositionEditable = false
            updateGeometrySize()
        }
    }

    var frame by remember { mutableStateOf<Frame?>(null) }

    var arSceneWidth by remember { mutableIntStateOf(0) }
    var arSceneHeight by remember { mutableIntStateOf(0) }

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
            add(crosshairNode)
            addAll(markerNodes)
            addAll(lineNodes)
            addAll(lineLabelNodes)
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
                val pose = hitResult.hitPose
                crosshairNode.worldPosition = pose.position

                val flatCorrection = Quaternion.fromAxisAngle(Float3(1f, 0f, 0f), -90f)
                crosshairNode.quaternion = pose.quaternion * flatCorrection

                val correction = Quaternion.fromAxisAngle(Float3(1f, 0f, 0f), 0f)
                val correctedQuaternion = pose.quaternion * correction

                val correctedPose = Pose(
                    floatArrayOf(pose.tx(), pose.ty(), pose.tz()),
                    floatArrayOf(
                        correctedQuaternion.x,
                        correctedQuaternion.y,
                        correctedQuaternion.z,
                        correctedQuaternion.w
                    )
                )

                onCrosshairMove(hitResult.trackable, correctedPose, cameraNode.worldPosition)
            }
        }
    )
}