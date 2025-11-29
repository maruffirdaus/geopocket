package dev.maruffirdaus.geopocket.ui.ar.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.google.android.filament.Engine
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.node.ViewNode2

@Composable
fun rememberNodeManager(
    engine: Engine,
    materialLoader: MaterialLoader,
    windowManager: ViewNode2.WindowManager,
    lineLabelContent: @Composable (Float) -> Unit
): NodeManager {
    val nodeManager = remember(engine, materialLoader, windowManager, lineLabelContent) {
        NodeManager(engine, materialLoader, windowManager, lineLabelContent)
    }

    DisposableEffect(Unit) {
        onDispose {
            nodeManager.destroy()
        }
    }

    return nodeManager
}