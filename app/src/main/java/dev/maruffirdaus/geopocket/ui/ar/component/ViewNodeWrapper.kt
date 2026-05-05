package dev.maruffirdaus.geopocket.ui.ar.component

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme
import io.github.sceneview.SceneScope
import io.github.sceneview.collision.CollisionShape
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.ViewNode

@Composable
fun SceneScope.ViewNodeWrapper(
    windowManager: ViewNode.WindowManager,
    visible: Boolean,
    position: Position = Position(x = 0f),
    rotation: Rotation = Rotation(x = 0f),
    collisionShape: CollisionShape? = null,
    isPositionEditable: Boolean = collisionShape != null,
    content: @Composable () -> Unit
) {
    ViewNode(
        windowManager = windowManager,
        unlit = true,
        position = position,
        rotation = rotation,
        isVisible = visible,
        apply = {
            pxPerUnits = 2000f
            this.collisionShape = collisionShape
            this.isPositionEditable = isPositionEditable
        }
    ) {
        GeoPocketTheme {
            Box(
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    }
}