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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.ar.core.Pose
import com.google.ar.core.Trackable
import dev.maruffirdaus.geopocket.R
import dev.maruffirdaus.geopocket.ui.ar.component.CustomArScene
import dev.maruffirdaus.geopocket.ui.ar.component.LineLabel
import dev.maruffirdaus.geopocket.ui.ar.component.rememberNodeManager
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme
import io.github.sceneview.math.Position
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberViewNodeManager
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ArScreen(
    navController: NavHostController,
    viewModel: ArViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val engine = rememberEngine()
    val materialLoader = rememberMaterialLoader(engine)
    val windowManager = rememberViewNodeManager()

    val nodeManager = rememberNodeManager(engine, materialLoader, windowManager) { length ->
        GeoPocketTheme {
            Box(contentAlignment = Alignment.Center) {
                LineLabel("%.2f".format(length) + " m")
            }
        }
    }

    val markerNodes by nodeManager.markerNodes.collectAsStateWithLifecycle()
    val lineNodes by nodeManager.lineNodes.collectAsStateWithLifecycle()
    val lineLabelNodes by nodeManager.lineLabelNodes.collectAsStateWithLifecycle()

    var currentTrackable by remember { mutableStateOf<Trackable?>(null) }
    var currentPose by remember { mutableStateOf<Pose?>(null) }
    var currentCamPos by remember { mutableStateOf<Position?>(null) }

    ArScreenContent(
        uiState = uiState,
        emptyNodes = markerNodes.isEmpty(),
        arScene = {
            CustomArScene(
                markerNodes = markerNodes,
                lineNodes = lineNodes,
                lineLabelNodes = lineLabelNodes,
                onCrosshairMove = { trackable, pose, camPos ->
                    currentTrackable = trackable
                    currentPose = pose
                    currentCamPos = camPos
                },
                engine = engine,
                materialLoader = materialLoader,
                windowManager = windowManager
            )
        },
        onBack = {
            navController.popBackStack()
        },
        onNodeAdd = {
            if (markerNodes.size < 2) {
                val trackable = currentTrackable
                val pose = currentPose
                val camPos = currentCamPos

                if (trackable != null && pose != null && camPos != null) {
                    nodeManager.createMarkerNode(trackable.createAnchor(pose), camPos)
                }
            } else {
                viewModel.changeErrorMessage("You cannot place more than 2 nodes")
            }
        },
        onNodesClear = nodeManager::clearNodes,
        onErrorMessageChange = viewModel::changeErrorMessage
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ArScreenContent(
    uiState: ArUiState,
    emptyNodes: Boolean,
    arScene: @Composable () -> Unit,
    onBack: () -> Unit,
    onNodeAdd: () -> Unit,
    onNodesClear: () -> Unit,
    onErrorMessageChange: (String?) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            snackbarHostState.showSnackbar(uiState.errorMessage)
            onErrorMessageChange(null)
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
                        onClick = onNodesClear,
                        enabled = !emptyNodes
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
                    onClick = onNodeAdd,
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
            IconButton(
                onClick = onBack,
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
                colors = IconButtonDefaults.iconButtonColors(
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
private fun ArScreenPreview() {
    GeoPocketTheme {
        ArScreenContent(
            uiState = ArUiState(),
            emptyNodes = true,
            arScene = {},
            onBack = {},
            onNodeAdd = {},
            onNodesClear = {},
            onErrorMessageChange = {}
        )
    }
}