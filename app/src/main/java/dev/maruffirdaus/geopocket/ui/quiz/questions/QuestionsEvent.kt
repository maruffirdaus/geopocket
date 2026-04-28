package dev.maruffirdaus.geopocket.ui.quiz.questions

sealed interface QuestionsEvent {
    data class OnSelectOption(val index: Int, val id: String) : QuestionsEvent
    object OnFinish : QuestionsEvent
}