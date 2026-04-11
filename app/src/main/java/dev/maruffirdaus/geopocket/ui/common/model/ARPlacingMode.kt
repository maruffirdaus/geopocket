package dev.maruffirdaus.geopocket.ui.common.model

enum class ARPlacingMode(
    val maxNodes: Int
) {
    LINE(2),
    TRIANGLE(3),
    RECTANGLE(4)
}