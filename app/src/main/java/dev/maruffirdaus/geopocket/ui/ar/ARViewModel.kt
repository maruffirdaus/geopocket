package dev.maruffirdaus.geopocket.ui.ar

import androidx.lifecycle.ViewModel
import com.google.ar.core.Pose
import dev.maruffirdaus.geopocket.ui.ar.model.SegmentNode
import dev.maruffirdaus.geopocket.ui.ar.model.PointNode
import io.github.sceneview.ar.arcore.position
import io.github.sceneview.ar.arcore.quaternion
import io.github.sceneview.math.Position
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class ARViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ARUiState())
    val uiState = _uiState.asStateFlow()

    private var isPreviewSegmentEnabled = true

    private var currentPose: Pose? = null
    private var currentCamPos: Position? = null

    fun onEvent(event: AREvent) {
        when (event) {
            is AREvent.OnUpdatePlacementIndicator -> onUpdatePlacementIndicator(
                event.pose,
                event.camPos
            )

            AREvent.OnAddPoint -> onAddPoint()
            is AREvent.OnPointMoved -> onPointMoved(event.id, event.pose)
            AREvent.OnClearPoints -> onClearPoints()
            is AREvent.OnUpdateErrorMessage -> onUpdateErrorMessage(event.message)
        }
    }

    private fun onUpdatePlacementIndicator(pose: Pose, camPos: Position) {
        _uiState.update { state ->
            val previewSegment = if (isPreviewSegmentEnabled) {
                state.points.values.lastOrNull()?.let { lastMarker ->
                    SegmentNode(
                        startPos = lastMarker.worldPosition,
                        endPos = pose.position,
                        camPos = camPos
                    )
                }
            } else null

            state.copy(
                previewSegment = previewSegment
            )
        }

        currentPose = pose
        currentCamPos = camPos
    }

    private fun onAddPoint() {
        val pose = currentPose ?: return
        val camPos = currentCamPos ?: return

        var marker = PointNode(worldPosition = pose.position, quaternion = pose.quaternion)
        val lastMarker = uiState.value.points.values.lastOrNull()

        if (lastMarker != null) {
            val line = SegmentNode(
                startPos = lastMarker.worldPosition,
                endPos = marker.worldPosition,
                camPos = camPos,
                startMarkerId = lastMarker.id,
                endMarkerId = marker.id
            )
            marker = marker.copy(connectedLineIds = listOf(line.id))

            _uiState.update {
                val updatedMarkers = mapOf(
                    lastMarker.id to lastMarker.copy(connectedLineIds = lastMarker.connectedLineIds + line.id),
                    marker.id to marker
                )
                it.copy(
                    points = it.points + updatedMarkers,
                    segments = it.segments + (line.id to line)
                )
            }
        } else {
            _uiState.update {
                it.copy(points = it.points + (marker.id to marker))
            }
        }
    }

    private fun onPointMoved(id: String, pose: Pose) {
        val marker = uiState.value.points[id] ?: return
        val camPos = currentCamPos ?: return

        val updatedLines = mutableMapOf<String, SegmentNode>()

        marker.connectedLineIds.forEach { lineId ->
            val line = uiState.value.segments[lineId] ?: return@forEach
            val isStart = line.startMarkerId == id
            val startPos = if (isStart) {
                pose.position
            } else {
                uiState.value.points[line.startMarkerId]?.worldPosition ?: return@forEach
            }
            val endPos = if (isStart) {
                uiState.value.points[line.endMarkerId]?.worldPosition ?: return@forEach
            } else {
                pose.position
            }

            updatedLines[lineId] = line.copy(startPos = startPos, endPos = endPos, camPos = camPos)
        }

        _uiState.update {
            it.copy(
                points = it.points + (id to marker.copy(worldPosition = pose.position)),
                segments = it.segments + updatedLines
            )
        }
    }

    private fun onClearPoints() {
        _uiState.update {
            it.copy(
                previewSegment = null,
                points = mapOf(),
                segments = mapOf()
            )
        }
    }

    private fun onUpdateErrorMessage(message: String?) {
        _uiState.update {
            it.copy(errorMessage = message)
        }
    }
}