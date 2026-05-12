package dev.maruffirdaus.geopocket.ui.ar.navigation

import androidx.navigation3.runtime.NavKey
import dev.maruffirdaus.geopocket.domain.topic.Subtopic
import dev.maruffirdaus.geopocket.domain.topic.result.AngleResult
import dev.maruffirdaus.geopocket.domain.topic.result.SegmentResult
import kotlinx.serialization.Serializable

@Serializable
sealed interface ARNavKey : NavKey {
    @Serializable
    object Activity : ARNavKey

    @Serializable
    data class Result(
        val subtopic: Subtopic,
        val segments: Map<String, SegmentResult>,
        val angles: Map<String, AngleResult>,
        val completionImage: String
    ) : ARNavKey
}