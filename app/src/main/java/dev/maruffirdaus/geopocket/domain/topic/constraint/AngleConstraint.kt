package dev.maruffirdaus.geopocket.domain.topic.constraint

data class AngleConstraint(
    val id: String,
    val minDegree: Float? = null,
    val maxDegree: Float? = null
) {
    companion object {
        const val TOLERANCE = 1.6f
    }
}
