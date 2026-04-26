package dev.maruffirdaus.geopocket.ui.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme

@Composable
fun PageIndicator(
    size: Int,
    currentIndex: Int,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(CircleShape),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(size) { index ->
            val isCurrentIndex = index == currentIndex
            val color = if (isCurrentIndex) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.surfaceContainer
            }

            Box(
                modifier = Modifier
                    .height(12.dp)
                    .weight(1f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
                    .clickable { onClick(index) }
            )
        }
    }
}

@Composable
@Preview
private fun PageIndicatorPreview() {
    GeoPocketTheme {
        PageIndicator(
            size = 4,
            currentIndex = 0,
            onClick = {}
        )
    }
}