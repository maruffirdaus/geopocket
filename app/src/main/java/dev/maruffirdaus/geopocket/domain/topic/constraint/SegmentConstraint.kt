package dev.maruffirdaus.geopocket.domain.topic.constraint

data class SegmentConstraint(
    val id: String,
    val minLength: Float? = null,
    val maxLength: Float? = null
) {
    companion object {
        const val TOLERANCE = 0.005f
    }
}
