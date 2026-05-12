package dev.maruffirdaus.geopocket.ui.navigation

import androidx.navigation3.runtime.NavKey
import dev.maruffirdaus.geopocket.domain.topic.Subtopic
import dev.maruffirdaus.geopocket.domain.topic.result.AngleResult
import dev.maruffirdaus.geopocket.domain.topic.result.SegmentResult
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppNavKey : NavKey {
    @Serializable
    object Home : AppNavKey

    @Serializable
    data class Topic(
        val topic: dev.maruffirdaus.geopocket.domain.topic.Topic
    ) : AppNavKey

    @Serializable
    data class Instructions(
        val subtopic: Subtopic
    ) : AppNavKey

    @Serializable
    data class AR(
        val subtopic: Subtopic
    ) : AppNavKey

    @Serializable
    data class Quiz(
        val subtopic: Subtopic,
        val segments: Map<String, SegmentResult>,
        val angles: Map<String, AngleResult>
    ) : AppNavKey

    @Serializable
    object Settings : AppNavKey

    @Serializable
    object Licenses : AppNavKey
}