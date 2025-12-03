package dev.maruffirdaus.geopocket.ui.common.model

enum class ArPlacingMode(
    val maxNodes: Int
) {
    LINE(2),
    TRIANGLE(3),
    RECTANGLE(4)
}