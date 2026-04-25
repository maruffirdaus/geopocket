package dev.maruffirdaus.geopocket.ui.ar

import androidx.lifecycle.ViewModel
import com.google.ar.core.Pose
import dev.maruffirdaus.geopocket.domain.topic.Subtopic
import dev.maruffirdaus.geopocket.ui.ar.model.AngleNodeState
import dev.maruffirdaus.geopocket.ui.ar.model.PointNodeState
import dev.maruffirdaus.geopocket.ui.ar.model.PreviewState
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

    private var currentPose: Pose? = null
    private var currentCamPos: Position? = null

    fun onEvent(event: AREvent) {
        when (event) {
            is AREvent.OnUpdateReticle -> onUpdateReticle(event.pose, event.camPos)
            AREvent.OnAddPoint -> onAddPoint()
            is AREvent.OnPointMoved -> onPointMoved(event.id, event.pose)
            AREvent.OnClearPoints -> onClearPoints()
        }
    }

    private fun onUpdateReticle(pose: Pose, camPos: Position) {
        if (uiState.value.points.size >= subtopic.constraint.pointCount) return

        _uiState.update { state ->
            val correction = Quaternion.fromAxisAngle(Float3(1f, 0f, 0f), -90f)
            val segmentPreview = state.points.values.lastOrNull()?.let { lastPoint ->
                SegmentNodeState(
                    startPointId = lastPoint.id,
                    endPointId = "Z",
                    startPos = lastPoint.worldPosition,
                    endPos = pose.position,
                    camPos = camPos
                )
            }
            var closingSegmentPreview: SegmentNodeState? = null
            var anglePreview: AngleNodeState? = null
            var closingAnglePreview: AngleNodeState? = null

            val size = state.points.size

            if (size > 1) {
                val lastPoint = state.points[('A' + size - 1).toString()]
                val prevPoint = state.points[('A' + size - 2).toString()]

                if (lastPoint != null && prevPoint != null) {
                    anglePreview = AngleNodeState(
                        centerPoint = lastPoint,
                        startPos = prevPoint.worldPosition,
                        endPos = pose.position
                    )
                }

                val isClosing =
                    size + 1 == subtopic.constraint.pointCount && subtopic.constraint.closedShape
                val firstWorldPos = state.points["A"]?.worldPosition

                if (isClosing && firstWorldPos != null) {
                    closingSegmentPreview = SegmentNodeState(
                        startPointId = "Z",
                        endPointId = "A",
                        startPos = pose.position,
                        endPos = firstWorldPos,
                        camPos = camPos
                    )

                    val lastWorldPos = state.points.values.lastOrNull()?.worldPosition

                    if (lastWorldPos != null) {
                        closingAnglePreview = AngleNodeState(
                            id = "Z",
                            startPos = lastWorldPos,
                            centerPos = pose.position,
                            endPos = firstWorldPos,
                            quaternion = pose.quaternion * correction
                        )
                    }
                }
            }

            state.copy(
                reticle = ReticleNodeState(
                    worldPosition = pose.position,
                    quaternion = pose.quaternion * correction
                ),
                preview = PreviewState(
                    segment = segmentPreview,
                    closingSegment = closingSegmentPreview,
                    angle = anglePreview,
                    closingAngle = closingAnglePreview
                )
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
            id = ('A' + uiState.value.points.size).toString(),
            worldPosition = pose.position,
            quaternion = pose.quaternion,
        )
        var lastPoint = uiState.value.points.values.lastOrNull()

        if (lastPoint != null) {
            val segment = SegmentNodeState(
                startPointId = lastPoint.id,
                endPointId = point.id,
                startPos = lastPoint.worldPosition,
                endPos = point.worldPosition,
                camPos = camPos,
                constraint = subtopic.constraint.segments.getOrNull(lastPoint.id.first() - 'A')
            )
            point = point.copy(connectedSegmentIds = setOf(segment.id))
            lastPoint = lastPoint.copy(
                connectedSegmentIds = lastPoint.connectedSegmentIds + segment.id
            )

            val angle = if (uiState.value.points.size > 1) {
                val startPoint = uiState.value.points[(lastPoint.id.first() - 1).toString()]
                startPoint?.let {
                    AngleNodeState(
                        centerPoint = lastPoint,
                        startPos = startPoint.worldPosition,
                        endPos = point.worldPosition,
                        constraint = subtopic.constraint.angles.getOrNull(lastPoint.id.first() - 'B')
                    )
                }
            } else null

            var closingSegment: SegmentNodeState? = null
            val closingAngles: MutableList<AngleNodeState> = mutableListOf()

            val isPointCountReached =
                uiState.value.points.size + 1 == subtopic.constraint.pointCount
            val isClosing = isPointCountReached && subtopic.constraint.closedShape

            var firstPoint = uiState.value.points["A"]

            if (isClosing && firstPoint != null) {
                closingSegment = SegmentNodeState(
                    startPointId = point.id,
                    endPointId = firstPoint.id,
                    startPos = point.worldPosition,
                    endPos = firstPoint.worldPosition,
                    camPos = camPos,
                    constraint = subtopic.constraint.segments.lastOrNull()
                )
                point = point.copy(
                    connectedSegmentIds = point.connectedSegmentIds + closingSegment.id
                )
                firstPoint = firstPoint.copy(
                    connectedSegmentIds = firstPoint.connectedSegmentIds + closingSegment.id
                )

                closingAngles += AngleNodeState(
                    centerPoint = point,
                    startPos = lastPoint.worldPosition,
                    endPos = firstPoint.worldPosition,
                    constraint = subtopic.constraint.angles.getOrNull(point.id.first() - 'B')
                )
                uiState.value.points["B"]?.let { secondPoint ->
                    closingAngles += AngleNodeState(
                        centerPoint = firstPoint,
                        startPos = point.worldPosition,
                        endPos = secondPoint.worldPosition,
                        constraint = subtopic.constraint.angles.lastOrNull()
                    )
                }
            }

            _uiState.update { state ->
                val updatedPoints = buildMap {
                    put(lastPoint.id, lastPoint)
                    put(point.id, point)
                    firstPoint?.let { put(it.id, it) }
                }
                val updatedSegments = buildMap {
                    put(segment.id, segment)
                    closingSegment?.let { put(it.id, it) }
                }
                val updatedAngles = buildMap {
                    angle?.let { put(it.id, it) }
                    closingAngles.forEach { put(it.id, it) }
                }
                state.copy(
                    reticle = if (isPointCountReached) null else state.reticle,
                    preview = if (isPointCountReached) null else state.preview,
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
                    add(segment.startPointId)
                    add(segment.endPointId)
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
                angle.copy(
                    startPos = startPoint.worldPosition,
                    centerPos = centerPoint.worldPosition,
                    endPos = endPoint.worldPosition
                )
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
                preview = null,
                points = mapOf(),
                segments = mapOf(),
                angles = mapOf()
            )
        }
    }
}