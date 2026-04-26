package dev.maruffirdaus.geopocket.domain.topic.constraint

data class Constraint(
    val pointCount: Int,
    val closedShape: Boolean = false,
    val segments: Map<String, SegmentConstraint> = emptyMap(),
    val angles: Map<String, AngleConstraint> = emptyMap()
)