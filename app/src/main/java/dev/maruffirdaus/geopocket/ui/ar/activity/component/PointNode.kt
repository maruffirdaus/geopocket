package dev.maruffirdaus.geopocket.ui.ar.activity.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.ar.core.Anchor
import com.google.ar.core.Pose
import dev.maruffirdaus.geopocket.ui.ar.activity.model.ARConstants
import dev.maruffirdaus.geopocket.ui.ar.activity.model.PointNodeState
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme
import io.github.sceneview.ar.ARSceneScope
import io.github.sceneview.collision.Sphere
import io.github.sceneview.math.toRotation
import io.github.sceneview.node.ViewNode

@Composable
fun ARSceneScope.PointNode(
    state: PointNodeState,
    anchor: Anchor,
    windowManager: ViewNode.WindowManager,
    onPointMoving: (Pose) -> Unit,
    onPointMoved: () -> Unit
) {
    AnchorNode(
        anchor = anchor,
        onUpdated = { onPointMoved() },
        apply = {
            onPoseChanged = onPointMoving
        }
    ) {
        ViewNodeWrapper(
            windowManager = windowManager,
            rotation = ARConstants.FaceUpQuaternion.toRotation(),
            collisionShape = Sphere(0.1f),
            visible = true
        ) {
            PointNodeContent(state.id)
        }
    }
}

@Composable
private fun PointNodeContent(
    label: String,
    size: Dp = 32.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .border(1.dp, Color.Black, CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color.Black,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
@Preview
private fun PointNodePreview() {
    GeoPocketTheme {
        PointNodeContent("A")
    }
}