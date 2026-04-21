package dev.maruffirdaus.geopocket.ui.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.maruffirdaus.geopocket.domain.topic.Topic
import dev.maruffirdaus.geopocket.ui.common.component.TopicStatusLabel
import dev.maruffirdaus.geopocket.ui.common.model.TopicStatus
import dev.maruffirdaus.geopocket.ui.home.extension.toIcon
import dev.maruffirdaus.geopocket.ui.home.extension.toIconContainerShape
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TopicCard(
    topic: Topic,
    unlocked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    progress: Float = 0f
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        enabled = unlocked,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(topic.toIconContainerShape())
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(
                                alpha = if (!unlocked) 0.38f else 1f
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = topic.toIcon(),
                        contentDescription = topic.title,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onPrimary.copy(
                            alpha = if (!unlocked) 0.38f else 1f
                        )
                    )
                }
                TopicStatusLabel(
                    status = when {
                        !unlocked -> TopicStatus.LOCKED
                        progress >= 1f -> TopicStatus.PASSED
                        else -> TopicStatus.NOT_PASSED
                    }
                )

            }
            Spacer(Modifier.height(24.dp))
            Text(
                text = topic.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = topic.description,
                style = MaterialTheme.typography.bodySmall
            )
            if (unlocked) {
                Spacer(Modifier.height(24.dp))
                LinearWavyProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
@Preview
private fun TopicCardPreview() {
    GeoPocketTheme {
        TopicCard(
            topic = Topic.ANGLE,
            unlocked = true,
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            progress = 0.5f
        )
    }
}