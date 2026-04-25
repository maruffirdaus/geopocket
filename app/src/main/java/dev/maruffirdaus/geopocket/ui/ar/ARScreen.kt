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
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.ArrowLeft
import com.adamglin.phosphoricons.regular.Plus
import com.adamglin.phosphoricons.regular.Trash
import dev.maruffirdaus.geopocket.ui.ar.component.AppARSceneView
import dev.maruffirdaus.geopocket.ui.navigation.NavHandler
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ARScreen(
    viewModel: ARViewModel = koinViewModel(),
    navHandler: NavHandler = koinInject()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ARScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        navHandler = navHandler
    ) {
        AppARSceneView(
            reticle = uiState.reticle,
            preview = uiState.preview,
            points = uiState.points,
            segments = uiState.segments,
            angles = uiState.angles,
            onUpdateReticle = { pose, camPos ->
                viewModel.onEvent(AREvent.OnUpdateReticle(pose, camPos))
            },
            onPointMoved = { id, pose ->
                viewModel.onEvent(AREvent.OnPointMoved(id, pose))
            }
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ARScreenContent(
    uiState: ARUiState,
    onEvent: (AREvent) -> Unit,
    navHandler: NavHandler,
    arContent: @Composable () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalFloatingToolbar(
                    expanded = false,
                    collapsedShadowElevation = 1.dp
                ) {
                    IconButton(
                        onClick = {
                            onEvent(AREvent.OnClearPoints)
                        },
                        enabled = uiState.points.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = PhosphorIcons.Regular.Trash,
                            contentDescription = "Clear"
                        )
                    }
                }
                Spacer(Modifier.width(8.dp))
                FloatingActionButton(
                    onClick = {
                        onEvent(AREvent.OnAddPoint)
                    },
                    elevation = FloatingActionButtonDefaults.elevation(1.dp, 1.dp, 1.dp, 1.dp)
                ) {
                    Icon(
                        imageVector = PhosphorIcons.Regular.Plus,
                        contentDescription = "Add"
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            arContent()
            FilledIconButton(
                onClick = {
                    navHandler.pop()
                },
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
                    imageVector = PhosphorIcons.Regular.ArrowLeft,
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
            onEvent = {},
            navHandler = NavHandler()
        ) {}
    }
}