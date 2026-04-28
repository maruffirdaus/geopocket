package dev.maruffirdaus.geopocket.ui.common.extensions

import androidx.compose.ui.geometry.Offset

fun Offset.midpoint(other: Offset): Offset = Offset((x + other.x) / 2f, (y + other.y) / 2f)