package dev.maruffirdaus.geopocket.ui.quiz.scratchpad

import androidx.ink.strokes.Stroke

sealed interface ScratchpadEvent {
    data class OnStrokesFinished(val strokes: List<Stroke>) : ScratchpadEvent
}