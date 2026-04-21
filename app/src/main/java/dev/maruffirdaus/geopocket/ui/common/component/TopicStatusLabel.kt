package dev.maruffirdaus.geopocket.ui.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.maruffirdaus.geopocket.ui.common.model.TopicStatus
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme

@Composable
fun TopicStatusLabel(
    status: TopicStatus,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(28.dp)
            .clip(CircleShape)
            .background(
                color = MaterialTheme.colorScheme.secondary.copy(
                    alpha = if (status == TopicStatus.LOCKED) 0.38f else 1f
                )
            )
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = status.icon,
            contentDescription = status.title,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSecondary.copy(
                alpha = if (status == TopicStatus.LOCKED) 0.38f else 1f
            )
        )
        Text(
            text = status.title,
            color = MaterialTheme.colorScheme.onSecondary.copy(
                alpha = if (status == TopicStatus.LOCKED) 0.38f else 1f
            ),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
@Preview
private fun TopicStatusLabelPreview() {
    GeoPocketTheme {
        TopicStatusLabel(
            status = TopicStatus.LOCKED
        )
    }
}