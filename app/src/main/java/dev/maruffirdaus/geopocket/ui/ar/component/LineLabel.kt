package dev.maruffirdaus.geopocket.ui.ar.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme

@Composable
fun LineLabel(
    text: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100))
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
private fun LineLabelPreview() {
    GeoPocketTheme {
        LineLabel("0.99 m")
    }
}