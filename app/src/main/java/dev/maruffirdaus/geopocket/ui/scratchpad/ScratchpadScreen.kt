package dev.maruffirdaus.geopocket.ui.scratchpad

import android.graphics.Matrix
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
import dev.maruffirdaus.geopocket.ui.navigation.NavHandler
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

@Composable
fun ScratchpadScreenContent(
    uiState: ScratchpadUiState,
    onEvent: (ScratchpadEvent) -> Unit,
    navHandler: NavHandler
) {
    val density = LocalDensity.current

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(innerPadding)
        ) {
            val defaultBrush = Brush.createWithComposeColor(
                family = StockBrushes.marker(),
                color = MaterialTheme.colorScheme.primary,
                size = with(density) { 4.dp.toPx() },
                epsilon = 0.1F
            )
            val canvasStrokeRenderer = remember { CanvasStrokeRenderer.create() }

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
            InProgressStrokes(defaultBrush) { strokes ->
                onEvent(ScratchpadEvent.OnStrokesFinished(strokes))
            }
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
private fun ScratchpadScreenPreview() {
    GeoPocketTheme {
        ScratchpadScreenContent(
            uiState = ScratchpadUiState(),
            onEvent = {},
            navHandler = NavHandler()
        )
    }
}