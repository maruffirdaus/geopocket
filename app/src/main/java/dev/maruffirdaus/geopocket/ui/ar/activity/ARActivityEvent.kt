package dev.maruffirdaus.geopocket.ui.ar.activity

import com.google.ar.core.Pose
import io.github.sceneview.math.Position

sealed interface ARActivityEvent {
    data class OnUpdateReticle(val pose: Pose, val camPos: Position) : ARActivityEvent
    object OnEnablePreview : ARActivityEvent
    object OnEnablePlaneRenderer : ARActivityEvent
    object OnAddPoint : ARActivityEvent
    data class OnPointMoving(val id: String, val pose: Pose) : ARActivityEvent
    object OnPointMoved : ARActivityEvent
    object OnClearPoints : ARActivityEvent
}