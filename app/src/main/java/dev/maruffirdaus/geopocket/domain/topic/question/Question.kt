package dev.maruffirdaus.geopocket.domain.topic.question

data class Question(
    val text: String,
    val options: List<QuestionOption>,
    val correctOptionId: String
)