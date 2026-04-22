package dev.maruffirdaus.geopocket.ui.scratchpad

import androidx.ink.strokes.Stroke

data class ScratchpadUiState(
    val strokes: List<Stroke> = listOf()
)
