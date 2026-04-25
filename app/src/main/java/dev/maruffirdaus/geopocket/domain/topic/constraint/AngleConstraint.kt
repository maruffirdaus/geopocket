package dev.maruffirdaus.geopocket.domain.topic.constraint

data class AngleConstraint(
    val minDegree: Float? = null,
    val maxDegree: Float? = null,
    val tolerance: Float = 5f
)
