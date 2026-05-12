package dev.maruffirdaus.geopocket.ui.quiz.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface QuizNavKey : NavKey {
    @Serializable
    object Questions : QuizNavKey

    @Serializable
    object Scratchpad : QuizNavKey

    @Serializable
    data class Result(
        val score: Int,
        val completed: Boolean
    ) : QuizNavKey
}