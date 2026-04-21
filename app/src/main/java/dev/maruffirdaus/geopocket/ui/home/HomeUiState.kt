package dev.maruffirdaus.geopocket.ui.home

import dev.maruffirdaus.geopocket.data.local.subtopic.SubtopicProgress

data class HomeUiState(
    val subtopicProgresses: List<SubtopicProgress> = listOf()
)
