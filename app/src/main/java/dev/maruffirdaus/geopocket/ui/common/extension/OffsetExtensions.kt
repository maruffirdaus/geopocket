package dev.maruffirdaus.geopocket.ui.common.extension

import androidx.compose.ui.geometry.Offset

fun Offset.midpoint(other: Offset): Offset = Offset((x + other.x) / 2f, (y + other.y) / 2f)