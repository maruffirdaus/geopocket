package dev.maruffirdaus.geopocket.domain.topic.constraint

data class Constraint(
    val pointCount: Int,
    val closedShape: Boolean = false,
    val segments: List<SegmentConstraint> = emptyList(),
    val angles: List<AngleConstraint> = emptyList()
)