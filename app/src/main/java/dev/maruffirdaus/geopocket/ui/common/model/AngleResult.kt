package dev.maruffirdaus.geopocket.ui.common.model

import kotlinx.serialization.Serializable

@Serializable
data class AngleResult(
    val id: String,
    val degree: Float
)
