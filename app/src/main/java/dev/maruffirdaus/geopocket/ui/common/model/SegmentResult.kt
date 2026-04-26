package dev.maruffirdaus.geopocket.ui.common.model

import kotlinx.serialization.Serializable

@Serializable
data class SegmentResult(
    val id: String,
    val length: Float
)
