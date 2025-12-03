package dev.maruffirdaus.geopocket.ui.home.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.maruffirdaus.geopocket.ui.home.model.HomeItem
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme

@Composable
fun HomeCard(
    item: HomeItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Spacer(Modifier.height(24.dp))
        Icon(
            painter = painterResource(item.icon),
            contentDescription = item.title,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = item.title,
            modifier = Modifier.padding(horizontal = 24.dp),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
@Preview
private fun HomeCardPreview() {
    GeoPocketTheme {
        HomeCard(
            item = HomeItem.LINE,
            onClick = {}
        )
    }
}