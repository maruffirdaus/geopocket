package dev.maruffirdaus.geopocket.domain.topic.constraint

data class Constraint(
    val nodeCount: Int,
    val lines: List<LineConstraint> = emptyList(),
    val angles: List<AngleConstraint> = emptyList()
)