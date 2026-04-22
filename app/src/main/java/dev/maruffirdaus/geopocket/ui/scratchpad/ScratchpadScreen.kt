package dev.maruffirdaus.geopocket.ui.scratchpad

import android.graphics.Matrix
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.ink.authoring.compose.InProgressStrokes
import androidx.ink.brush.Brush
import androidx.ink.brush.StockBrushes
import androidx.ink.rendering.android.canvas.CanvasStrokeRenderer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.ArrowLeft
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ScratchpadScreen(
    onNavigateBack: () -> Unit,
    viewModel: ScratchpadViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ScratchpadScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun ScratchpadScreenContent(
    uiState: ScratchpadUiState,
    onEvent: (ScratchpadEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Coret")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            imageVector = PhosphorIcons.Regular.ArrowLeft,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                val defaultBrush = Brush.createWithColorIntArgb(
                    family = StockBrushes.marker(),
                    colorIntArgb = MaterialTheme.colorScheme.primary.toArgb(),
                    size = 15F,
                    epsilon = 0.1F
                )
                val canvasStrokeRenderer = remember { CanvasStrokeRenderer.create() }

                InProgressStrokes(defaultBrush) { strokes ->
                    onEvent(ScratchpadEvent.OnStrokesFinished(strokes))
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
            onNavigateBack = {}
        )
    }
}