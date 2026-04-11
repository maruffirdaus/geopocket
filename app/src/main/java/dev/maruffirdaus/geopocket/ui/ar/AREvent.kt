package dev.maruffirdaus.geopocket.ui.ar

import com.google.ar.core.Pose
import com.google.ar.core.Trackable
import io.github.sceneview.math.Position

sealed interface AREvent {
    data class OnPlacementIndicatorUpdate(val pose: Pose, val camPos: Position) : AREvent
    object OnMarkerAdd : AREvent
    data class OnMarkerMove(val id: String, val pose: Pose) : AREvent
    object OnMarkersClear : AREvent
    data class OnErrorMessageUpdate(val message: String?) : AREvent
}