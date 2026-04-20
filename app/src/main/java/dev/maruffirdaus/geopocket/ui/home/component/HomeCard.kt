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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.maruffirdaus.geopocket.ui.home.model.HomeItem
import dev.maruffirdaus.geopocket.ui.home.model.HomeItemStatus
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeCard(
    item: HomeItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        enabled = item.status != HomeItemStatus.LOCKED,
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
                        .clip(CircleShape)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(
                                alpha = if (item.status == HomeItemStatus.LOCKED) 0.38f else 1f
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onPrimary.copy(
                            alpha = if (item.status == HomeItemStatus.LOCKED) 0.38f else 1f
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            color = MaterialTheme.colorScheme.secondary.copy(
                                alpha = if (item.status == HomeItemStatus.LOCKED) 0.38f else 1f
                            )
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = item.status.icon,
                        contentDescription = item.status.title,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSecondary.copy(
                            alpha = if (item.status == HomeItemStatus.LOCKED) 0.38f else 1f
                        )
                    )
                    Text(
                        text = item.status.title,
                        color = MaterialTheme.colorScheme.onSecondary.copy(
                            alpha = if (item.status == HomeItemStatus.LOCKED) 0.38f else 1f
                        ),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall
            )
            if (item.status != HomeItemStatus.LOCKED) {
                Spacer(Modifier.height(24.dp))
                LinearProgressIndicator(
                    progress = {
                        if (item.status == HomeItemStatus.COMPLETED) {
                            1f
                        } else {
                            0.5f
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
@Preview
private fun HomeCardPreview() {
    GeoPocketTheme {
        HomeCard(
            item = HomeItem.ANGLE,
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}