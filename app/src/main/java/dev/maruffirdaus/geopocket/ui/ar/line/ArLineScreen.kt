package dev.maruffirdaus.geopocket.ui.ar.line

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Undo
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import dev.maruffirdaus.geopocket.ui.ar.line.component.ArLineScene
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ArLineScreen(
    navController: NavHostController,
    viewModel: ArLineViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ArLineScreenContent(
        uiState = uiState,
        arScene = {
            ArLineScene(
                markerNodes = uiState.markerNodes,
                lineNodes = uiState.lineNodes,
                onMarkerNodeCreate = viewModel::createMarkerNode
            )
        },
        onBack = {
            navController.popBackStack()
        },
        onMarkerNodeUndo = viewModel::undoMarkerNode,
        onMarkerNodesClear = viewModel::clearMarkerNodes,
        onErrorMessageChange = viewModel::changeErrorMessage
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ArLineScreenContent(
    uiState: ArLineUiState,
    arScene: @Composable () -> Unit,
    onBack: () -> Unit,
    onMarkerNodeUndo: () -> Unit,
    onMarkerNodesClear: () -> Unit,
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
            HorizontalFloatingToolbar(
                expanded = false,
                collapsedShadowElevation = 1.dp
            ) {
                IconButton(
                    onClick = onMarkerNodeUndo,
                    enabled = uiState.markerNodes.isNotEmpty()
                ) {
                    Icon(Icons.AutoMirrored.Outlined.Undo, "Undo")
                }
                IconButton(
                    onClick = onMarkerNodesClear,
                    enabled = uiState.markerNodes.isNotEmpty()
                ) {
                    Icon(Icons.Outlined.Clear, "Clear")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { _ ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            arScene()
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .systemBarsPadding()
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
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                )
            }
        }
    }
}

@Composable
@Preview
private fun ArLineScreenPreview() {
    GeoPocketTheme {
        ArLineScreenContent(
            uiState = ArLineUiState(),
            arScene = {},
            onBack = {},
            onMarkerNodeUndo = {},
            onMarkerNodesClear = {},
            onErrorMessageChange = {}
        )
    }
}