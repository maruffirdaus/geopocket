package dev.maruffirdaus.geopocket.ui.quiz.questions

sealed interface QuestionsEvent {
    data class OnSelectOption(val index: Int, val id: String) : QuestionsEvent
    data class OnFinish(val onResultSaved: (Int, Boolean) -> Unit) : QuestionsEvent
}