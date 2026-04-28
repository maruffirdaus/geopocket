package dev.maruffirdaus.geopocket.domain.topic.result

import kotlinx.serialization.Serializable

@Serializable
data class SegmentResult(
    val id: String,
    val length: Float
)
