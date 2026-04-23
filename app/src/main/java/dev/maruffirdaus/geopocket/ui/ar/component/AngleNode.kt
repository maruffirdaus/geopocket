package dev.maruffirdaus.geopocket.ui.ar.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.maruffirdaus.geopocket.ui.ar.model.AngleNodeState
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.SceneScope
import io.github.sceneview.math.toRotation
import io.github.sceneview.node.ViewNode

@Composable
fun SceneScope.AngleNode(
    state: AngleNodeState,
    windowManager: ViewNode.WindowManager
) {
    val correction = Quaternion.fromAxisAngle(Float3(1f, 0f, 0f), -90f)

    ViewNodeWrapper(
        windowManager = windowManager,
        position = state.worldPosition,
        rotation = (state.quaternion * correction).toRotation()
    ) {
        AngleNodeContent("${state.degree.toInt()}°")
    }
}

@Composable
private fun AngleNodeContent(
    label: String
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .border(1.dp, Color.Black, CircleShape)
            .background(Color.White)
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = label,
            color = Color.Black,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
@Preview
private fun AngleNodePreview() {
    GeoPocketTheme {
        AngleNodeContent("90°")
    }
}