package dev.maruffirdaus.geopocket.ui.quiz.questions

import dev.maruffirdaus.geopocket.domain.topic.Subtopic
import dev.maruffirdaus.geopocket.domain.topic.question.Question
import dev.maruffirdaus.geopocket.domain.topic.result.AngleResult
import dev.maruffirdaus.geopocket.domain.topic.result.SegmentResult

data class QuestionsUiState(
    val subtopic: Subtopic = Subtopic.LINE_SEGMENT,
    val segments: Map<String, SegmentResult> = mapOf(),
    val angles: Map<String, AngleResult> = mapOf(),
    val questions: List<Question> = listOf(),
    val selectedOptionIds: Map<Int, String> = mapOf(),
    val score: Int? = null
)
