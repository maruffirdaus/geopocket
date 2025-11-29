package dev.maruffirdaus.geopocket.ui.ar.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme

@Composable
fun Crosshair(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    overallSize: Dp = 64.dp,
    dotSize: Dp = 4.dp,
    strokeWidth: Dp = 4.dp,
    gapAngle: Float = 20f
) {
    Canvas(modifier = modifier.size(overallSize)) {
        val arcSize = this.size.minDimension - strokeWidth.toPx()
        val padding = strokeWidth.toPx() / 2

        drawCircle(
            color = color,
            radius = dotSize.toPx()
        )

        val sweepAngle = 90f - gapAngle
        val startOffset = gapAngle / 2f

        repeat(4) { index ->
            drawArc(
                color = color,
                startAngle = (index * 90f) + startOffset,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(padding, padding),
                size = Size(arcSize, arcSize),
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
@Preview
private fun CrosshairPreview() {
    GeoPocketTheme {
        Crosshair()
    }
}