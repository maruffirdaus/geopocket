package dev.maruffirdaus.geopocket.ui.settings.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.maruffirdaus.geopocket.ui.settings.model.SettingItem

@Composable
fun SettingsGroup(
    items: List<SettingItem>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(16.dp)),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    .clickable(onClick = item.onClick)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item.icon?.let { icon ->
                    Icon(
                        imageVector = icon,
                        contentDescription = item.title
                    )
                }
                Column {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = item.description,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}