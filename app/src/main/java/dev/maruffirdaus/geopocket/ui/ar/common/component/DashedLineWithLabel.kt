package dev.maruffirdaus.geopocket.ui.ar.common.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme

@Composable
fun DashedLineWithLabel(
    label: String,
    modifier: Modifier = Modifier,
    strokeWidth: Float = 4f
) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier.height(strokeWidth.dp)) {
            val canvasWidth = size.width
            val canvasHeight = size.height / 2

            drawLine(
                color = Color.Black,
                start = Offset(0f, canvasHeight),
                end = Offset(canvasWidth, canvasHeight),
                strokeWidth = strokeWidth,
                pathEffect = PathEffect.dashPathEffect(
                    floatArrayOf(10f, 10f),
                    0f
                )
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(100))
                .background(Color.White)
                .padding(4.dp)
        ) {
            Text(
                text = label,
                color = Color.Black,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
@Preview
private fun DashedLineWithLabelPreview() {
    GeoPocketTheme {
        DashedLineWithLabel(
            "0.99 m",
            modifier = Modifier.fillMaxWidth()
        )
    }
}