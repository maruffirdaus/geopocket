package dev.maruffirdaus.geopocket.ui.quiz.scratchpad

import android.graphics.Matrix
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.ink.authoring.compose.InProgressStrokes
import androidx.ink.brush.Brush
import androidx.ink.brush.StockBrushes
import androidx.ink.brush.compose.createWithComposeColor
import androidx.ink.rendering.android.canvas.CanvasStrokeRenderer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.ArrowLeft
import com.adamglin.phosphoricons.regular.Trash
import dev.maruffirdaus.geopocket.ui.navigation.NavHandler
import dev.maruffirdaus.geopocket.ui.quiz.navigation.QuizNavKey
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ScratchpadScreen(
    viewModel: ScratchpadViewModel = koinViewModel(),
    navHandler: NavHandler = koinInject()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ScratchpadScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        navHandler = navHandler
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ScratchpadScreenContent(
    uiState: ScratchpadUiState,
    onEvent: (ScratchpadEvent) -> Unit,
    navHandler: NavHandler
) {
    val density = LocalDensity.current

    val brushColors = listOf(
        if (isSystemInDarkTheme()) Color.White else Color.Black,
        Color.Red,
        Color.Green,
        Color.Blue
    )

    Scaffold(
        floatingActionButton = {
            HorizontalFloatingToolbar(
                expanded = false,
                collapsedShadowElevation = 1.dp
            ) {
                brushColors.forEachIndexed { index, color ->
                    val isSelected = index == uiState.selectedBrushColorIndex

                    IconButton(
                        onClick = {
                            onEvent(ScratchpadEvent.OnSelectBrushColor(index))
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 40.dp else 24.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }
                IconButton(
                    onClick = {
                        onEvent(ScratchpadEvent.OnClearStrokes)
                    },
                ) {
                    Icon(
                        imageVector = PhosphorIcons.Regular.Trash,
                        contentDescription = "Clear"
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(innerPadding)
        ) {
            val brushFamily = StockBrushes.marker()
            val brushSize = with(density) { 4.dp.toPx() }
            val brushEpsilon = 0.1f

            val defaultBrush = Brush.createWithComposeColor(
                family = brushFamily,
                color = brushColors[0],
                size = brushSize,
                epsilon = brushEpsilon
            )
            var nextBrush: Brush? by remember { mutableStateOf(null) }

            val canvasStrokeRenderer = remember { CanvasStrokeRenderer.create() }

            LaunchedEffect(uiState.selectedBrushColorIndex) {
                nextBrush = uiState.selectedBrushColorIndex.let {
                    Brush.createWithComposeColor(
                        family = brushFamily,
                        color = brushColors[it],
                        size = brushSize,
                        epsilon = brushEpsilon
                    )
                }
            }

            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                drawIntoCanvas { canvas ->
                    uiState.strokes.forEach { stroke ->
                        canvasStrokeRenderer.draw(
                            stroke = stroke,
                            canvas = canvas.nativeCanvas,
                            strokeToScreenTransform = Matrix()
                        )
                    }
                }
            }
            InProgressStrokes(
                defaultBrush = defaultBrush,
                nextBrush = {
                    nextBrush ?: defaultBrush
                }
            ) { strokes ->
                onEvent(ScratchpadEvent.OnStrokesFinished(strokes))
            }
            FilledIconButton(
                onClick = {
                    navHandler.pop<QuizNavKey>()
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
private fun ScratchpadScreenPreview() {
    GeoPocketTheme {
        ScratchpadScreenContent(
            uiState = ScratchpadUiState(),
            onEvent = {},
            navHandler = NavHandler()
        )
    }
}