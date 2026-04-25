package dev.maruffirdaus.geopocket.domain.topic.constraint

data class SegmentConstraint(
    val minLength: Float? = null,
    val maxLength: Float? = null,
    val tolerance: Float = 0.02f
)
