package dev.maruffirdaus.geopocket.ui.ar.node

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.google.android.filament.Engine
import dev.maruffirdaus.geopocket.ui.common.model.ArPlacingMode
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.node.ViewNode2

@Composable
fun rememberNodeManager(
    engine: Engine,
    materialLoader: MaterialLoader,
    windowManager: ViewNode2.WindowManager,
    mode: ArPlacingMode,
    crosshairContent: @Composable () -> Unit,
    lineLabelContent: @Composable (Float) -> Unit
): NodeManager {
    val nodeManager = remember(
        engine,
        materialLoader,
        windowManager,
        mode,
        crosshairContent,
        lineLabelContent
    ) {
        NodeManager(
            engine,
            materialLoader,
            windowManager,
            mode,
            crosshairContent,
            lineLabelContent
        )
    }
    return nodeManager
}