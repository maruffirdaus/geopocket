package dev.maruffirdaus.geopocket.ui.ar.node

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.google.android.filament.Engine
import dev.maruffirdaus.geopocket.ui.common.model.ArPlacingMode
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.node.ViewNode2
import kotlinx.coroutines.launch

@Composable
fun rememberNodeManager(
    engine: Engine,
    modelLoader: ModelLoader,
    materialLoader: MaterialLoader,
    windowManager: ViewNode2.WindowManager,
    mode: ArPlacingMode,
    crosshairContent: @Composable () -> Unit,
    lineLabelContent: @Composable (Float) -> Unit
): NodeManager {
    val nodeManager = remember(
        engine,
        modelLoader,
        materialLoader,
        windowManager,
        mode,
        crosshairContent,
        lineLabelContent
    ) {
        NodeManager(
            engine,
            modelLoader,
            materialLoader,
            windowManager,
            mode,
            crosshairContent,
            lineLabelContent
        )
    }
    val scope = rememberCoroutineScope()

    scope.launch {
        nodeManager.loadMaterial()
    }

    return nodeManager
}