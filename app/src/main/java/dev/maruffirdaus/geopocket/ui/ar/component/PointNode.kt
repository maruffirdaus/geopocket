package dev.maruffirdaus.geopocket.ui.ar.component

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
import dev.maruffirdaus.geopocket.ui.ar.model.PointNodeState
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.ar.ARSceneScope
import io.github.sceneview.collision.Sphere
import io.github.sceneview.math.toRotation
import io.github.sceneview.node.ViewNode

@Composable
fun ARSceneScope.PointNode(
    state: PointNodeState,
    anchor: Anchor,
    windowManager: ViewNode.WindowManager,
    onPointMoved: (Pose) -> Unit
) {
    val rotation = Quaternion.fromAxisAngle(Float3(1f, 0f, 0f), -90f)

    AnchorNode(
        anchor = anchor,
        onUpdated = {
            onPointMoved(it.pose)
        }
    ) {
        ViewNodeWrapper(
            windowManager = windowManager,
            rotation = rotation.toRotation(),
            collisionShape = Sphere(0.1f)
        ) {
            PointNodeContent(state.label)
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