package dev.maruffirdaus.geopocket.ui.instructions

import dev.maruffirdaus.geopocket.domain.topic.Subtopic

data class InstructionsUiState(
    val subtopic: Subtopic = Subtopic.LINE_SEGMENT,
    val stepIndex: Int = 0
)
