package dev.maruffirdaus.geopocket.ui.topic.component

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
import androidx.compose.material3.Card
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
import dev.maruffirdaus.geopocket.domain.topic.Subtopic
import dev.maruffirdaus.geopocket.ui.common.component.TopicStatusLabel
import dev.maruffirdaus.geopocket.ui.common.model.TopicStatus
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme
import dev.maruffirdaus.geopocket.ui.theme.LocalExtendedColors

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SubtopicCard(
    subtopic: Subtopic,
    unlocked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    highestScore: Int? = null,
    completed: Boolean = false
) {
    val extendedColors = LocalExtendedColors.current

    Card(
        onClick = onClick,
        modifier = modifier,
        enabled = unlocked,
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (unlocked && highestScore != null) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
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
                            text = highestScore.toString(),
                            color = if (completed) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                extendedColors.fail.onColor
                            },
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Spacer(Modifier.weight(1f))
                }
                TopicStatusLabel(
                    status = when {
                        !unlocked -> TopicStatus.LOCKED
                        completed -> TopicStatus.PASSED
                        else -> TopicStatus.NOT_PASSED
                    }
                )
            }
            Spacer(Modifier.height(24.dp))
            Text(
                text = subtopic.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = subtopic.description,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
@Preview
private fun SubtopicCardPreview() {
    GeoPocketTheme {
        SubtopicCard(
            subtopic = Subtopic.ANGLE_OBTUSE,
            unlocked = true,
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            highestScore = 75
        )
    }
}