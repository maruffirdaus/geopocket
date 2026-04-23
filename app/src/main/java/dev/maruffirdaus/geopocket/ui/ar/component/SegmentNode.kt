package dev.maruffirdaus.geopocket.ui.ar.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.maruffirdaus.geopocket.ui.ar.model.SegmentNodeState
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.SceneScope
import io.github.sceneview.math.toRotation
import io.github.sceneview.node.ViewNode

@Composable
fun SceneScope.SegmentNode(
    state: SegmentNodeState,
    windowManager: ViewNode.WindowManager
) {
    val locale = LocalLocale.current.platformLocale

    val localUp = state.quaternion * Float3(0f, 0f, 1f)

    val blackMaterial = remember(materialLoader) {
        materialLoader.createColorInstance(Color.Black)
    }

    val cubeWidth = 0.0025f
    val cubeHeight = 0.0001f

    CubeNode(
        size = Float3(1f, cubeWidth, cubeHeight),
        materialInstance = blackMaterial,
        position = state.worldPosition,
        rotation = state.quaternion.toRotation(),
        scale = state.scale
    )
    ViewNodeWrapper(
        windowManager = windowManager,
        position = state.worldPosition + localUp * cubeHeight,
        rotation = state.quaternion.toRotation()
    ) {
        Label("${String.format(locale, "%.0f", state.length * 100)} cm")
    }
}

@Composable
private fun Label(text: String) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .border(1.dp, Color.Black, CircleShape)
            .background(Color.White)
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = text,
            color = Color.Black,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
@Preview
private fun LabelPreview() {
    GeoPocketTheme {
        Label("100 cm")
    }
}