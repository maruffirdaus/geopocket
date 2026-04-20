package dev.maruffirdaus.geopocket.ui.common.model

enum class ARPlacingMode(
    val maxNodes: Int
) {
    LINE(2),
    ANGLE(3),
    TRIANGLE(3),
    QUADRILATERAL(4)
}