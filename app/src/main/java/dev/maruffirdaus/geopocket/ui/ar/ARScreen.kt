package dev.maruffirdaus.geopocket.ui.ar

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.ArrowLeft
import com.adamglin.phosphoricons.regular.Plus
import com.adamglin.phosphoricons.regular.Trash
import dev.maruffirdaus.geopocket.ui.ar.component.AppARSceneView
import dev.maruffirdaus.geopocket.ui.ar.extension.capture
import dev.maruffirdaus.geopocket.ui.ar.extension.saveToCache
import dev.maruffirdaus.geopocket.ui.navigation.NavHandler
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme
import kotlinx.coroutines.launch
import me.saket.telephoto.zoomable.coil3.ZoomableAsyncImage
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ARScreen(
    viewModel: ARViewModel = koinViewModel(),
    navHandler: NavHandler = koinInject()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val activity = LocalActivity.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.completed) {
        if (uiState.completed) {
            repeat(4) { withFrameNanos {} }
            activity?.capture(
                onSuccess = { bitmap ->
                    scope.launch {
                        val file = bitmap.saveToCache(activity)
                        bitmap.recycle()
                        viewModel.onEvent(AREvent.OnCompletionImageCaptured(file.absolutePath))
                    }
                },
                onError = {
                    navHandler.pop()
                }
            )
        }
    }

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
        topBar = {
            if (uiState.completionImage != null)
                LargeFlexibleTopAppBar(
                    title = {
                        Text("Berhasil")
                    },
                    subtitle = {
                        Text("Kamu telah menyelesaikan aktivitas ini dengan baik")
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navHandler.pop()
                            }
                        ) {
                            Icon(
                                imageVector = PhosphorIcons.Regular.ArrowLeft,
                                contentDescription = "Kembali"
                            )
                        }
                    }
                )
        },
        floatingActionButton = {
            if (!uiState.completed)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
        if (uiState.completionImage != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ZoomableAsyncImage(
                    model = uiState.completionImage,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Mulai kuis")
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                arContent()
                if (!uiState.completed) {
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
    }
}

@Composable
@Preview
private fun ARScreenPreview() {
    GeoPocketTheme {
        ARScreenContent(
            uiState = ARUiState(
                completionImage = ""
            ),
            onEvent = {},
            navHandler = NavHandler()
        ) {}
    }
}