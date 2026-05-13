package dev.maruffirdaus.geopocket.ui.common.extension

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.alignHorizontalSpace(padding: Dp = 0.dp) = then(
    Modifier
        .fillMaxWidth()
        .wrapContentWidth(align = Alignment.CenterHorizontally)
        .padding(horizontal = padding)
        .widthIn(max = 600.dp)
        .fillMaxWidth()
)