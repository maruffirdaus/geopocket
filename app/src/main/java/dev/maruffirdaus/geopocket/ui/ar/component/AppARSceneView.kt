package dev.maruffirdaus.geopocket.ui.ar.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import com.google.android.filament.ColorGrading
import com.google.android.filament.ToneMapper
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Pose
import dev.maruffirdaus.geopocket.ui.ar.model.PointNodeState
import dev.maruffirdaus.geopocket.ui.ar.model.ReticleNodeState
import dev.maruffirdaus.geopocket.ui.ar.model.SegmentNodeState
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.math.Position
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberView
import io.github.sceneview.rememberViewNodeManager

private const val HIT_TEST_INTERVAL_MS = 100L

@Composable
fun AppARSceneView(
    reticle: ReticleNodeState?,
    previewSegment: SegmentNodeState?,
    points: Map<String, PointNodeState>,
    segments: Map<String, SegmentNodeState>,
    onUpdateReticle: (Pose, Position) -> Unit,
    onPointMoved: (String, Pose) -> Unit
) {
    var width by remember { mutableIntStateOf(0) }
    var height by remember { mutableIntStateOf(0) }

    val engine = rememberEngine()
    val materialLoader = rememberMaterialLoader(engine)

    val colorGrading = remember(engine) {
        ColorGrading.Builder()
            .toneMapper(ToneMapper.Linear())
            .build(engine)
    }
    val view = rememberView(engine).apply {
        this.colorGrading = colorGrading
    }

    val cameraNode = rememberARCameraNode(engine)
    val windowManager = rememberViewNodeManager()

    val anchors = remember { mutableStateMapOf<String, Anchor>() }

    var lastHitTestMs by remember { mutableLongStateOf(0L) }

    LaunchedEffect(points) {
        val anchorsToRemove = anchors.keys - points.keys
        anchorsToRemove.forEach { id ->
            anchors[id]?.detach()
            anchors.remove(id)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            anchors.values.forEach { it.detach() }
            anchors.clear()
        }
    }

    ARSceneView(
        modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
            width = layoutCoordinates.size.width
            height = layoutCoordinates.size.height
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
            config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
        },
        planeRenderer = false,
        view = view,
        cameraNode = cameraNode,
        viewNodeWindowManager = windowManager,
        onSessionUpdated = { session, frame ->
            val currentTimeMs = System.currentTimeMillis()

            if (currentTimeMs - lastHitTestMs >= HIT_TEST_INTERVAL_MS) {
                lastHitTestMs = currentTimeMs

                val centerX = width / 2f
                val centerY = height / 2f

                if (centerX > 0f && centerY > 0f) {
                    val hitResult = frame
                        .hitTest(centerX, centerY)
                        .firstOrNull { it.isValid(depthPoint = false, point = false) }

                    if (hitResult != null) {
                        onUpdateReticle(hitResult.hitPose, cameraNode.worldPosition)
                    }
                }
            }

            points.values.forEach { marker ->
                if (marker.id in anchors) return@forEach
                val pose = Pose(
                    floatArrayOf(
                        marker.worldPosition.x,
                        marker.worldPosition.y,
                        marker.worldPosition.z
                    ),
                    floatArrayOf(
                        marker.quaternion.x,
                        marker.quaternion.y,
                        marker.quaternion.z,
                        marker.quaternion.w
                    )
                )
                anchors[marker.id] = session.createAnchor(pose)
            }
        }
    ) {
        reticle?.let {
            key("reticle") {
                ReticleNode(
                    state = it,
                    windowManager = windowManager
                )
            }
        }
        previewSegment?.let {
            key("previewSegment") {
                SegmentNode(
                    state = it,
                    windowManager = windowManager
                )
            }
        }
        anchors.forEach { (id, anchor) ->
            key(id) {
                points[id]?.let { point ->
                    PointNode(
                        state = point,
                        anchor = anchor,
                        windowManager = windowManager,
                        onPointMoved = { pose ->
                            onPointMoved(id, pose)
                        }
                    )
                }
            }
        }
        segments.values.forEach { segment ->
            key(segment.id) {
                SegmentNode(
                    state = segment,
                    windowManager = windowManager
                )
            }
        }
    }
}