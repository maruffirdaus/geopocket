package dev.maruffirdaus.geopocket.ui.ar.component

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme

@Composable
fun ViewNode2Container(
    content: @Composable () -> Unit
) {
    GeoPocketTheme {
        Box(contentAlignment = Alignment.Center) {
            content()
        }
    }
}