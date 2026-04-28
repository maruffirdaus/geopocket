package dev.maruffirdaus.geopocket.domain.topic.result

import kotlinx.serialization.Serializable

@Serializable
data class AngleResult(
    val id: String,
    val degree: Float
)
