package dev.maruffirdaus.geopocket.ui.ar

import com.google.ar.core.Pose
import io.github.sceneview.math.Position

sealed interface AREvent {
    data class OnUpdateReticle(val pose: Pose, val camPos: Position) : AREvent
    object OnEnablePreview : AREvent
    object OnEnablePlaneRenderer : AREvent
    object OnAddPoint : AREvent
    data class OnPointMoved(val id: String, val pose: Pose) : AREvent
    object OnClearPoints : AREvent
    data class OnCompletionImageCaptured(val path: String) : AREvent
    object OnComposableReady : AREvent
    object OnComposableDisposed : AREvent
}