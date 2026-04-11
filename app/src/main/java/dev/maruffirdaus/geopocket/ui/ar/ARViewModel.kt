package dev.maruffirdaus.geopocket.ui.ar

import androidx.lifecycle.ViewModel
import com.google.ar.core.Pose
import dev.maruffirdaus.geopocket.ui.ar.model.LineNode
import dev.maruffirdaus.geopocket.ui.ar.model.MarkerNode
import dev.maruffirdaus.geopocket.ui.ar.model.PlacementIndicatorNode
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.ar.arcore.position
import io.github.sceneview.ar.arcore.quaternion
import io.github.sceneview.math.Position
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ARViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ARUiState())
    val uiState = _uiState.asStateFlow()

    private var isPreviewLineEnabled = true

    private var currentPose: Pose? = null
    private var currentCamPos: Position? = null

    fun onEvent(event: AREvent) {
        when (event) {
            is AREvent.OnPlacementIndicatorUpdate -> updatePlacementIndicator(
                event.pose,
                event.camPos
            )

            AREvent.OnMarkerAdd -> addMarker()
            is AREvent.OnMarkerMove -> moveMarker(event.id, event.pose)
            AREvent.OnMarkersClear -> clearMarkers()
            is AREvent.OnErrorMessageUpdate -> updateErrorMessage(event.message)
        }
    }

    private fun updatePlacementIndicator(pose: Pose, camPos: Position) {
        val correction = Quaternion.fromAxisAngle(Float3(1f, 0f, 0f), -90f)
        val worldPosition = pose.position
        val quaternion = pose.quaternion * correction

        _uiState.update { state ->
            val previewLine = if (isPreviewLineEnabled) {
                state.markers.values.lastOrNull()?.let { lastMarker ->
                    LineNode(
                        startPos = lastMarker.worldPosition,
                        endPos = worldPosition,
                        camPos = camPos
                    )
                }
            } else null

            state.copy(
                placementIndicator = PlacementIndicatorNode(
                    worldPosition = worldPosition,
                    quaternion = quaternion
                ),
                previewLine = previewLine
            )
        }

        currentPose = uiState.value.placementIndicator?.calculateCorrectedPose()
        currentCamPos = camPos
    }

    private fun addMarker() {
        val pose = currentPose ?: return
        val camPos = currentCamPos ?: return

        var marker = MarkerNode(worldPosition = pose.position, quaternion = pose.quaternion)
        val lastMarker = uiState.value.markers.values.lastOrNull()

        if (lastMarker != null) {
            val line = LineNode(
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
                    markers = it.markers + updatedMarkers,
                    measurementLines = it.measurementLines + (line.id to line)
                )
            }
        } else {
            _uiState.update {
                it.copy(markers = it.markers + (marker.id to marker))
            }
        }
    }

    private fun moveMarker(id: String, pose: Pose) {
        val marker = uiState.value.markers[id] ?: return
        val camPos = currentCamPos ?: return

        val updatedLines = mutableMapOf<String, LineNode>()

        marker.connectedLineIds.forEach { lineId ->
            val line = uiState.value.measurementLines[lineId] ?: return@forEach
            val isStart = line.startMarkerId == id
            val startPos = if (isStart) {
                pose.position
            } else {
                uiState.value.markers[line.startMarkerId]?.worldPosition ?: return@forEach
            }
            val endPos = if (isStart) {
                uiState.value.markers[line.endMarkerId]?.worldPosition ?: return@forEach
            } else {
                pose.position
            }

            updatedLines[lineId] = line.copy(startPos = startPos, endPos = endPos, camPos = camPos)
        }

        _uiState.update {
            it.copy(
                markers = it.markers + (id to marker.copy(worldPosition = pose.position)),
                measurementLines = it.measurementLines + updatedLines
            )
        }
    }

    private fun clearMarkers() {
        _uiState.update {
            it.copy(
                previewLine = null,
                markers = mapOf(),
                measurementLines = mapOf()
            )
        }
    }

    private fun updateErrorMessage(message: String?) {
        _uiState.update {
            it.copy(errorMessage = message)
        }
    }
}