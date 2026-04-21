package dev.maruffirdaus.geopocket.ui.topic

import dev.maruffirdaus.geopocket.data.local.subtopic.SubtopicProgress
import dev.maruffirdaus.geopocket.domain.topic.Topic

data class TopicUiState(
    val topic: Topic = Topic.LINE,
    val subtopicProgresses: List<SubtopicProgress> = listOf()
)
