package dev.maruffirdaus.geopocket.ui.quiz.questions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.maruffirdaus.geopocket.data.local.subtopic.SubtopicProgress
import dev.maruffirdaus.geopocket.data.repository.TopicRepository
import dev.maruffirdaus.geopocket.domain.topic.Subtopic
import dev.maruffirdaus.geopocket.domain.topic.Topic
import dev.maruffirdaus.geopocket.domain.topic.result.AngleResult
import dev.maruffirdaus.geopocket.domain.topic.result.SegmentResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class QuestionsViewModel(
    @InjectedParam private val subtopic: Subtopic,
    @InjectedParam private val segments: Map<String, SegmentResult>,
    @InjectedParam private val angles: Map<String, AngleResult>,
    private val topicRepository: TopicRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        QuestionsUiState(
            subtopic = subtopic,
            segments = segments,
            angles = angles,
            questions = subtopic.questions(segments.values.toList(), angles.values.toList())
        )
    )
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: QuestionsEvent) {
        when (event) {
            is QuestionsEvent.OnSelectOption -> onSelectOption(event.index, event.id)
            is QuestionsEvent.OnFinish -> onFinish()
        }
    }

    private fun onSelectOption(index: Int, id: String) {
        _uiState.update {
            it.copy(selectedOptionIds = it.selectedOptionIds + mapOf(index to id))
        }
    }

    private fun onFinish() {
        val questions = uiState.value.questions
        val selectedOptionIds = uiState.value.selectedOptionIds
        var score = 0f

        questions.forEachIndexed { index, question ->
            if (selectedOptionIds[index] == question.correctOptionId) score++
        }

        val normalizedScore = (score / selectedOptionIds.size * 100).toInt()

        viewModelScope.launch {
            val lastScore = topicRepository.getSubtopicProgress(subtopic.id)?.highestScore
            val highestScore = maxOf(normalizedScore, lastScore ?: 0)
            val isCompleted = highestScore >= 75

            topicRepository.save(
                SubtopicProgress(
                    id = subtopic.id,
                    highestScore = highestScore,
                    isCompleted = isCompleted
                )
            )

            val nextSubtopic = subtopic.nextSubtopic()

            if (isCompleted && nextSubtopic != null) {
                topicRepository.save(
                    SubtopicProgress(id = nextSubtopic.id)
                )
            }
        }

        _uiState.update {
            it.copy(score = normalizedScore)
        }
    }

    private fun Subtopic.nextSubtopic(): Subtopic? {
        val nextInTopic = Subtopic.entries
            .filter { it.topic == topic && it.order > order }
            .minByOrNull { it.order }

        if (nextInTopic != null) return nextInTopic

        val nextTopic = Topic.entries.getOrNull(Topic.entries.indexOf(topic) + 1)
            ?: return null

        return Subtopic.entries
            .filter { it.topic == nextTopic }
            .minByOrNull { it.order }
    }
}