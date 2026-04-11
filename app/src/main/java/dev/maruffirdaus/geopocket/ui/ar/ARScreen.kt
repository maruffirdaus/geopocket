package dev.maruffirdaus.geopocket.ui.ar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.minimumInteractiveComponentSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.filament.ColorGrading
import com.google.android.filament.ToneMapper
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Pose
import dev.maruffirdaus.geopocket.R
import dev.maruffirdaus.geopocket.ui.ar.component.PlacementIndicator
import dev.maruffirdaus.geopocket.ui.common.model.ARPlacingMode
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.collision.Vector3
import io.github.sceneview.math.toRotation
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberView
import io.github.sceneview.rememberViewNodeManager
import org.koin.compose.viewmodel.koinViewModel

private const val HIT_TEST_INTERVAL_MS = 100L

@Composable
fun ARScreen(
    mode: ARPlacingMode,
    onNavigateBack: () -> Unit,
    viewModel: ARViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var arSceneWidth by remember { mutableIntStateOf(0) }
    var arSceneHeight by remember { mutableIntStateOf(0) }

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

    val whiteMaterial = remember(materialLoader) {
        materialLoader.createColorInstance(Color.White)
    }

    var lastHitTestMs by remember { mutableLongStateOf(0L) }

    LaunchedEffect(uiState.markers) {
        val anchorsToRemove = anchors.keys - uiState.markers.keys
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

    ARScreenContent(
        uiState = uiState,
        arScene = {
            ARSceneView(
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
                    config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                },
                planeRenderer = true,
                view = view,
                cameraNode = cameraNode,
                viewNodeWindowManager = windowManager,
                onSessionUpdated = { session, frame ->
                    val currentTimeMs = System.currentTimeMillis()

                    if (currentTimeMs - lastHitTestMs >= HIT_TEST_INTERVAL_MS) {
                        lastHitTestMs = currentTimeMs

                        val centerX = arSceneWidth / 2f
                        val centerY = arSceneHeight / 2f

                        if (centerX > 0f && centerY > 0f) {
                            val hitResult = frame
                                .hitTest(centerX, centerY)
                                .firstOrNull { it.isValid(depthPoint = false, point = false) }

                            if (hitResult != null) {
                                viewModel.onEvent(
                                    AREvent.OnPlacementIndicatorUpdate(
                                        hitResult.hitPose,
                                        cameraNode.worldPosition
                                    )
                                )
                            }
                        }
                    }

                    uiState.markers.values.forEach { marker ->
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
                uiState.placementIndicator?.let {
                    key("placementIndicator") {
                        ViewNode(
                            windowManager = windowManager,
                            unlit = true,
                            apply = {
                                position = it.worldPosition
                                quaternion = it.quaternion
                                pxPerUnits = 2000f
                                collisionShape = null
                                isPositionEditable = false
                            }
                        ) {
                            PlacementIndicator()
                        }
                    }
                }
                uiState.previewLine?.let {
                    key("previewLine") {
                        CubeNode(
                            materialInstance = whiteMaterial,
                            position = it.worldPosition,
                            rotation = it.quaternion.toRotation(),
                            scale = it.scale,
                            apply = {
                                collisionShape = null
                                isPositionEditable = false
                            }
                        )
//                        ViewNode(
//                            windowManager = windowManager,
//                            unlit = true,
//                            apply = {
//                                worldPosition = it.label.worldPosition
//                                quaternion = it.label.quaternion
//                                pxPerUnits = 2000f
//                                collisionShape = null
//                                isPositionEditable = false
//                            }
//                        ) {
//                            LineLabel(it.label.length)
//                        }
                    }
                }
                anchors.forEach { (id, anchor) ->
                    key(id) {
                        AnchorNode(
                            anchor = anchor,
                            onUpdated = {
                                viewModel.onEvent(AREvent.OnMarkerMove(id, it.pose))
                            }
                        ) {
                            CylinderNode(
                                radius = 0.005f,
                                height = 0.0001f,
                                materialInstance = whiteMaterial,
                                apply = {
                                    collisionShape =
                                        io.github.sceneview.collision.Box(Vector3(0.1f, 0.1f, 0.1f))
                                }
                            )
                        }
                    }
                }
                uiState.measurementLines.forEach { (id, line) ->
                    key(id) {
                        CubeNode(
                            materialInstance = whiteMaterial,
                            position = line.worldPosition,
                            rotation = line.quaternion.toRotation(),
                            scale = line.scale,
                            apply = {
                                collisionShape = null
                                isPositionEditable = false
                            }
                        )
//                        ViewNode(
//                            windowManager = windowManager,
//                            unlit = true,
//                            apply = {
//                                worldPosition = line.label.worldPosition
//                                quaternion = line.label.quaternion
//                                pxPerUnits = 2000f
//                                collisionShape = null
//                                isPositionEditable = false
//                            }
//                        ) {
//                            LineLabel(line.label.length)
//                        }
                    }
                }
            }
        },
        onNavigateBack = onNavigateBack,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ARScreenContent(
    uiState: ARUiState,
    arScene: @Composable () -> Unit,
    onNavigateBack: () -> Unit,
    onEvent: (AREvent) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            snackbarHostState.showSnackbar(uiState.errorMessage)
            onEvent(AREvent.OnErrorMessageUpdate(null))
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        floatingActionButton = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalFloatingToolbar(
                    expanded = false,
                    collapsedShadowElevation = 1.dp
                ) {
                    IconButton(
                        onClick = {
                            onEvent(AREvent.OnMarkersClear)
                        },
                        enabled = uiState.markers.isNotEmpty()
                    ) {
                        Icon(painterResource(R.drawable.ic_trash), "Clear")
                    }
                    IconButton(
                        onClick = {},
                        enabled = false
                    ) {
                        Icon(painterResource(R.drawable.ic_camera), "Take picture")
                    }
                }
                Spacer(Modifier.width(8.dp))
                FloatingActionButton(
                    onClick = {
                        onEvent(AREvent.OnMarkerAdd)
                    },
                    elevation = FloatingActionButtonDefaults.elevation(1.dp, 1.dp, 1.dp, 1.dp)
                ) {
                    Icon(painterResource(R.drawable.ic_plus), "Add")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            arScene()
            FilledIconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 4.dp, vertical = 8.dp)
                    .minimumInteractiveComponentSize()
                    .size(
                        IconButtonDefaults.smallContainerSize(
                            IconButtonDefaults.IconButtonWidthOption.Narrow
                        )
                    ),
                shape = IconButtonDefaults.smallRoundShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_left),
                    contentDescription = "Back",
                )
            }
        }
    }
}

@Composable
@Preview
private fun ARScreenPreview() {
    GeoPocketTheme {
        ARScreenContent(
            uiState = ARUiState(),
            arScene = {},
            onNavigateBack = {},
            onEvent = {}
        )
    }
}