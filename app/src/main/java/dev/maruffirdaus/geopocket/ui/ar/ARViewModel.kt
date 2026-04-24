package dev.maruffirdaus.geopocket.ui.ar

import androidx.lifecycle.ViewModel
import com.google.ar.core.Pose
import dev.maruffirdaus.geopocket.domain.topic.Subtopic
import dev.maruffirdaus.geopocket.ui.ar.model.AngleNodeState
import dev.maruffirdaus.geopocket.ui.ar.model.PointNodeState
import dev.maruffirdaus.geopocket.ui.ar.model.ReticleNodeState
import dev.maruffirdaus.geopocket.ui.ar.model.SegmentNodeState
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.ar.arcore.position
import io.github.sceneview.ar.arcore.quaternion
import io.github.sceneview.math.Position
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class ARViewModel(
    @InjectedParam private val subtopic: Subtopic
) : ViewModel() {
    private val _uiState = MutableStateFlow(ARUiState())
    val uiState = _uiState.asStateFlow()

    private var isPreviewSegmentEnabled = true

    private var currentPose: Pose? = null
    private var currentCamPos: Position? = null

    fun onEvent(event: AREvent) {
        when (event) {
            is AREvent.OnUpdateReticle -> onUpdateReticle(
                event.pose,
                event.camPos
            )

            AREvent.OnAddPoint -> onAddPoint()
            is AREvent.OnPointMoved -> onPointMoved(event.id, event.pose)
            AREvent.OnClearPoints -> onClearPoints()
            is AREvent.OnUpdateErrorMessage -> onUpdateErrorMessage(event.message)
        }
    }

    private fun onUpdateReticle(pose: Pose, camPos: Position) {
        if (uiState.value.points.size >= subtopic.constraint.pointCount) return

        _uiState.update { state ->
            val correction = Quaternion.fromAxisAngle(Float3(1f, 0f, 0f), -90f)
            val previewSegment = if (isPreviewSegmentEnabled) {
                state.points.values.lastOrNull()?.let { lastPoint ->
                    SegmentNodeState(
                        startPos = lastPoint.worldPosition,
                        endPos = pose.position,
                        camPos = camPos
                    )
                }
            } else null

            state.copy(
                reticle = ReticleNodeState(
                    worldPosition = pose.position,
                    quaternion = pose.quaternion * correction
                ),
                previewSegment = previewSegment
            )
        }

        currentPose = pose
        currentCamPos = camPos
    }

    private fun onAddPoint() {
        if (uiState.value.points.size >= subtopic.constraint.pointCount) return

        val pose = currentPose ?: return
        val camPos = currentCamPos ?: return

        var point = PointNodeState(
            worldPosition = pose.position,
            quaternion = pose.quaternion,
            label = ('A' + uiState.value.points.size).toString()
        )
        var lastPoint = uiState.value.points.values.lastOrNull()

        if (lastPoint != null) {
            val segment = SegmentNodeState(
                startPos = lastPoint.worldPosition,
                endPos = point.worldPosition,
                camPos = camPos,
                startPointId = lastPoint.id,
                endPointId = point.id
            )
            point = point.copy(connectedSegmentIds = setOf(segment.id))
            lastPoint =
                lastPoint.copy(connectedSegmentIds = lastPoint.connectedSegmentIds + segment.id)

            val angle = if (uiState.value.points.size > 1) {
                val startPointId = uiState.value.segments.values
                    .firstOrNull { it.endPointId == lastPoint.id }?.startPointId
                val startPoint = uiState.value.points[startPointId]
                startPoint?.let {
                    AngleNodeState(startPoint, lastPoint, point, lastPoint.id)
                }
            } else null

            var closingSegment: SegmentNodeState? = null
            val closingAngles: MutableList<AngleNodeState> = mutableListOf()

            val isClosingPoint = uiState.value.points.size + 1 == subtopic.constraint.pointCount

            var firstPoint: PointNodeState? = null

            if (isClosingPoint) {
                firstPoint = uiState.value.points.values.first { it.label == "A" }
                closingSegment = SegmentNodeState(
                    startPos = point.worldPosition,
                    endPos = firstPoint.worldPosition,
                    camPos = camPos,
                    startPointId = point.id,
                    endPointId = firstPoint.id
                )
                point =
                    point.copy(connectedSegmentIds = point.connectedSegmentIds + closingSegment.id)
                firstPoint =
                    firstPoint.copy(connectedSegmentIds = firstPoint.connectedSegmentIds + closingSegment.id)
                closingAngles += AngleNodeState(lastPoint, point, firstPoint, point.id)
                val secondPoint = uiState.value.points.values.first { it.label == "B" }
                closingAngles += AngleNodeState(point, firstPoint, secondPoint, firstPoint.id)
            }

            _uiState.update { state ->
                val updatedPoints = buildMap {
                    put(lastPoint.id, lastPoint)
                    put(point.id, point)
                    firstPoint?.let {
                        put(it.id, it)
                    }
                }
                val updatedSegments = buildMap {
                    put(segment.id, segment)
                    closingSegment?.let {
                        put(it.id, it)
                    }
                }
                val updatedAngles = buildMap {
                    angle?.let {
                        put(it.id, it)
                    }
                    closingAngles.forEach {
                        put(it.id, it)
                    }
                }
                state.copy(
                    reticle = if (isClosingPoint) null else state.reticle,
                    previewSegment = if (isClosingPoint) null else state.previewSegment,
                    points = state.points + updatedPoints,
                    segments = state.segments + updatedSegments,
                    angles = state.angles + updatedAngles
                )
            }
        } else {
            _uiState.update {
                it.copy(points = it.points + (point.id to point))
            }
        }
    }

    private fun onPointMoved(id: String, pose: Pose) {
        val point = uiState.value.points[id] ?: return
        val camPos = currentCamPos ?: return

        val updatedSegments = mutableMapOf<String, SegmentNodeState>()

        point.connectedSegmentIds.forEach { segmentId ->
            val segment = uiState.value.segments[segmentId] ?: return@forEach
            val isStart = segment.startPointId == id
            val startPos = if (isStart) {
                pose.position
            } else {
                uiState.value.points[segment.startPointId]?.worldPosition ?: return@forEach
            }
            val endPos = if (isStart) {
                uiState.value.points[segment.endPointId]?.worldPosition ?: return@forEach
            } else {
                pose.position
            }

            updatedSegments[segmentId] =
                segment.copy(startPos = startPos, endPos = endPos, camPos = camPos)
        }

        val updatedPoints =
            uiState.value.points + (id to point.copy(worldPosition = pose.position))
        val affectedPointIds = buildSet {
            add(id)
            uiState.value.segments.values
                .filter { it.startPointId == id || it.endPointId == id }
                .forEach { segment ->
                    segment.startPointId?.let { add(it) }
                    segment.endPointId?.let { add(it) }
                }
        }

        val updatedAngles = mutableMapOf<String, AngleNodeState>()

        affectedPointIds.forEach { pointId ->
            val angle = uiState.value.angles[pointId] ?: return@forEach
            val centerPoint = updatedPoints[pointId] ?: return@forEach

            val connectedSegments = uiState.value.segments.values
                .filter { it.startPointId == pointId || it.endPointId == pointId }
            if (connectedSegments.size < 2) return@forEach

            val startPointId = connectedSegments[0].let {
                if (it.startPointId == pointId) it.endPointId else it.startPointId
            }
            val endPointId = connectedSegments[1].let {
                if (it.startPointId == pointId) it.endPointId else it.startPointId
            }

            val startPoint = updatedPoints[startPointId] ?: return@forEach
            val endPoint = updatedPoints[endPointId] ?: return@forEach

            updatedAngles[pointId] =
                angle.copy(start = startPoint, center = centerPoint, end = endPoint)
        }

        _uiState.update {
            it.copy(
                points = it.points + (id to point.copy(worldPosition = pose.position)),
                segments = it.segments + updatedSegments,
                angles = it.angles + updatedAngles
            )
        }
    }

    private fun onClearPoints() {
        _uiState.update {
            it.copy(
                previewSegment = null,
                points = mapOf(),
                segments = mapOf(),
                angles = mapOf()
            )
        }
    }

    private fun onUpdateErrorMessage(message: String?) {
        _uiState.update {
            it.copy(errorMessage = message)
        }
    }
}