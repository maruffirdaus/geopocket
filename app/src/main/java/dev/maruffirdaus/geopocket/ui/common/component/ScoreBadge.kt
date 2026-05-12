package dev.maruffirdaus.geopocket.ui.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme
import dev.maruffirdaus.geopocket.ui.theme.LocalExtendedColors

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ScoreBadge(
    score: Int,
    completed: Boolean,
    modifier: Modifier = Modifier
) {
    val extendedColors = LocalExtendedColors.current

    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(1f)
            .clip(
                shape = if (completed) {
                    MaterialShapes.Cookie12Sided.toShape()
                } else {
                    MaterialShapes.Cookie9Sided.toShape()
                }
            )
            .background(
                color = if (completed) {
                    MaterialTheme.colorScheme.primary
                } else {
                    extendedColors.fail.color
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = score.toString(),
            color = if (completed) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                extendedColors.fail.onColor
            },
            fontSize = (maxWidth * 0.3f).value.sp
        )
    }
}

@Composable
@Preview
private fun ScoreBadgePreview() {
    GeoPocketTheme {
        ScoreBadge(
            score = 100,
            completed = true,
            modifier = Modifier.size(64.dp)
        )
    }
}