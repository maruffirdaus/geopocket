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
    private val _uiState = MutableStateFlow(ARUiState(subtopic = subtopic))
    val uiState = _uiState.asStateFlow()

    private var currentPose: Pose? = null
    private var currentCamPos: Position? = null

    private val quaternionCorrection = Quaternion.fromAxisAngle(Float3(1f, 0f, 0f), -90f)

    fun onEvent(event: AREvent) {
        when (event) {
            is AREvent.OnUpdateReticle -> onUpdateReticle(event.pose, event.camPos)
            AREvent.OnEnablePreview -> onEnablePreview()
            AREvent.OnEnablePlaneRenderer -> onEnablePlaneRenderer()
            AREvent.OnAddPoint -> onAddPoint()
            is AREvent.OnPointMoved -> onPointMoved(event.id, event.pose)
            AREvent.OnClearPoints -> onClearPoints()
            is AREvent.OnCompletionImageCaptured -> onCompletionImageCaptured(event.path)
        }
    }

    private fun onUpdateReticle(pose: Pose, camPos: Position) {
        if (uiState.value.points.size >= subtopic.constraint.pointCount) return

        currentPose = pose
        currentCamPos = camPos

        _uiState.update { state ->
            state.copy(
                reticle = ReticleNodeState(
                    worldPosition = pose.position,
                    quaternion = pose.quaternion * quaternionCorrection
                ),
                preview = if (state.previewEnabled) PreviewState(
                    segment = buildSegmentPreview(),
                    angle = buildAnglePreview(),
                    closingSegment = buildClosingSegmentPreview(),
                    closingAngle = buildClosingAnglePreview()
                ) else null,
                environmentScanned = true
            )
        }
    }

    private fun buildSegmentPreview(): SegmentNodeState? {
        val pose = currentPose ?: return null
        val camPos = currentCamPos ?: return null
        val lastPoint = _uiState.value.points.values.lastOrNull() ?: return null
        return SegmentNodeState(
            startPointId = lastPoint.id,
            endPointId = "Z",
            startPos = lastPoint.worldPosition,
            endPos = pose.position,
            camPos = camPos
        )
    }

    private fun buildAnglePreview(): AngleNodeState? {
        val pose = currentPose ?: return null
        val points = _uiState.value.points
        val size = points.size
        if (size <= 1) return null
        val lastPoint = points[('A' + size - 1).toString()] ?: return null
        val prevPoint = points[('A' + size - 2).toString()] ?: return null
        return AngleNodeState(
            id = lastPoint.id,
            startPos = prevPoint.worldPosition,
            centerPos = lastPoint.worldPosition,
            endPos = pose.position,
            quaternion = lastPoint.quaternion
        )
    }

    private fun buildClosingSegmentPreview(): SegmentNodeState? {
        if (!isClosing()) return null
        val pose = currentPose ?: return null
        val camPos = currentCamPos ?: return null
        val firstWorldPos = _uiState.value.points["A"]?.worldPosition ?: return null
        return SegmentNodeState(
            startPointId = "Z",
            endPointId = "A",
            startPos = pose.position,
            endPos = firstWorldPos,
            camPos = camPos
        )
    }

    private fun buildClosingAnglePreview(): AngleNodeState? {
        if (!isClosing()) return null
        val pose = currentPose ?: return null
        val points = _uiState.value.points
        val lastWorldPos = points.values.lastOrNull()?.worldPosition ?: return null
        val firstWorldPos = points["A"]?.worldPosition ?: return null
        return AngleNodeState(
            id = "Z",
            startPos = lastWorldPos,
            centerPos = pose.position,
            endPos = firstWorldPos,
            quaternion = pose.quaternion * quaternionCorrection
        )
    }

    private fun isPointCountReached(): Boolean {
        val points = uiState.value.points
        return points.size + 1 == subtopic.constraint.pointCount
    }

    private fun isClosing(): Boolean = isPointCountReached() && subtopic.constraint.closedShape

    private fun onEnablePreview() {
        _uiState.update {
            it.copy(
                previewEnabled = !it.previewEnabled,
                preview = null
            )
        }
    }

    private fun onEnablePlaneRenderer() {
        _uiState.update {
            it.copy(planeRendererEnabled = !it.planeRendererEnabled)
        }
    }

    private fun onAddPoint() {
        if (uiState.value.points.size >= subtopic.constraint.pointCount) return

        val pose = currentPose ?: return

        val point = PointNodeState(
            id = ('A' + uiState.value.points.size).toString(),
            worldPosition = pose.position,
            quaternion = pose.quaternion,
        )
        val lastPoint = uiState.value.points.values.lastOrNull()

        if (lastPoint == null) {
            _uiState.update {
                it.copy(points = it.points + (point.id to point))
            }
            updateCompletion()
            return
        }

        addPointWithConnections(point, lastPoint)
        updateCompletion()
    }

    private fun addPointWithConnections(point: PointNodeState, lastPoint: PointNodeState) {
        val isPointCountReached = isPointCountReached()
        val isClosing = isClosing()

        var updatedPoint = point
        var updatedLastPoint = lastPoint

        val segment = buildSegment(updatedLastPoint, updatedPoint) ?: return
        updatedPoint = updatedPoint.copy(connectedSegmentIds = setOf(segment.id))
        updatedLastPoint =
            updatedLastPoint.copy(connectedSegmentIds = updatedLastPoint.connectedSegmentIds + segment.id)

        val angle = buildAngle(updatedLastPoint, updatedPoint)

        val closingSegment =
            if (isClosing) buildClosingSegment(updatedPoint, updatedLastPoint) else null
        val closingAngles =
            if (isClosing) buildClosingAngles(updatedPoint, updatedLastPoint) else emptyList()

        var updatedFirstPoint =
            if (updatedLastPoint.id == "A") updatedLastPoint else uiState.value.points["A"]
        if (closingSegment != null && updatedFirstPoint != null) {
            updatedPoint =
                updatedPoint.copy(connectedSegmentIds = updatedPoint.connectedSegmentIds + closingSegment.id)
            updatedFirstPoint =
                updatedFirstPoint.copy(connectedSegmentIds = updatedFirstPoint.connectedSegmentIds + closingSegment.id)
        }

        _uiState.update { state ->
            state.copy(
                reticle = if (isPointCountReached) null else state.reticle,
                preview = if (isPointCountReached) null else state.preview,
                points = state.points + buildMap {
                    put(updatedLastPoint.id, updatedLastPoint)
                    put(updatedPoint.id, updatedPoint)
                    updatedFirstPoint?.let { put(it.id, it) }
                },
                segments = state.segments + buildMap {
                    put(segment.id, segment)
                    closingSegment?.let { put(it.id, it) }
                },
                angles = state.angles + buildMap {
                    angle?.let { put(it.id, it) }
                    closingAngles.forEach { put(it.id, it) }
                }
            )
        }
    }

    private fun buildSegment(lastPoint: PointNodeState, point: PointNodeState): SegmentNodeState? {
        val camPos = currentCamPos ?: return null
        return SegmentNodeState(
            startPointId = lastPoint.id,
            endPointId = point.id,
            startPos = lastPoint.worldPosition,
            endPos = point.worldPosition,
            camPos = camPos,
            constraint = subtopic.constraint.segments["${lastPoint.id}${point.id}"]
        )
    }

    private fun buildAngle(lastPoint: PointNodeState, point: PointNodeState): AngleNodeState? {
        if (uiState.value.points.size <= 1) return null
        val startPoint = uiState.value.points[(lastPoint.id.first() - 1).toString()] ?: return null
        return AngleNodeState(
            startPoint = startPoint,
            centerPoint = lastPoint,
            endPoint = point,
            constraint = subtopic.constraint.angles["${startPoint.id}${lastPoint.id}${point.id}"]
        )
    }

    private fun buildClosingSegment(
        point: PointNodeState,
        lastPoint: PointNodeState
    ): SegmentNodeState? {
        val camPos = currentCamPos ?: return null
        val firstPoint =
            if (lastPoint.id == "A") lastPoint else uiState.value.points["A"] ?: return null
        return SegmentNodeState(
            startPointId = point.id,
            endPointId = firstPoint.id,
            startPos = point.worldPosition,
            endPos = firstPoint.worldPosition,
            camPos = camPos,
            constraint = subtopic.constraint.segments["${point.id}${firstPoint.id}"]
        )
    }

    private fun buildClosingAngles(
        point: PointNodeState,
        lastPoint: PointNodeState
    ): List<AngleNodeState> {
        val firstPoint =
            if (lastPoint.id == "A") lastPoint else uiState.value.points["A"] ?: return emptyList()
        val angles = mutableListOf(
            AngleNodeState(
                startPoint = lastPoint,
                centerPoint = point,
                endPoint = firstPoint,
                constraint = subtopic.constraint.angles["${lastPoint.id}${point.id}${firstPoint.id}"]
            )
        )
        uiState.value.points["B"]?.let { secondPoint ->
            angles += AngleNodeState(
                startPoint = point,
                centerPoint = firstPoint,
                endPoint = secondPoint,
                constraint = subtopic.constraint.angles["${point.id}${firstPoint.id}${secondPoint.id}"]
            )
        }
        return angles
    }

    private fun onPointMoved(id: String, pose: Pose) {
        val point = uiState.value.points[id] ?: return

        val updatedPoints = uiState.value.points + (id to point.copy(worldPosition = pose.position))

        _uiState.update {
            it.copy(
                points = updatedPoints,
                segments = it.segments + buildUpdatedSegments(id, pose),
                angles = it.angles + buildUpdatedAngles(id, updatedPoints)
            )
        }
        updateCompletion()
    }

    private fun buildUpdatedSegments(id: String, pose: Pose): Map<String, SegmentNodeState> {
        val camPos = currentCamPos ?: return emptyMap()
        val point = uiState.value.points[id] ?: return emptyMap()
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

        return updatedSegments
    }

    private fun buildUpdatedAngles(
        id: String,
        updatedPoints: Map<String, PointNodeState>
    ): Map<String, AngleNodeState> {
        val updatedAngles = mutableMapOf<String, AngleNodeState>()

        uiState.value.angles.forEach { (_, angle) ->
            val startId = angle.id.substring(0, 1)
            val centerId = angle.id.substring(1, 2)
            val endId = angle.id.substring(2, 3)

            if (startId != id && centerId != id && endId != id) return@forEach

            val startPoint = updatedPoints[startId] ?: return@forEach
            val centerPoint = updatedPoints[centerId] ?: return@forEach
            val endPoint = updatedPoints[endId] ?: return@forEach

            updatedAngles[angle.id] = angle.copy(
                startPos = startPoint.worldPosition,
                centerPos = centerPoint.worldPosition,
                endPos = endPoint.worldPosition
            )
        }

        return updatedAngles
    }

    private fun updateCompletion() {
        val points = uiState.value.points
        val constraint = subtopic.constraint

        if (points.size < constraint.pointCount) return

        val segments = uiState.value.segments
        val angles = uiState.value.angles

        val areSegmentsValid = constraint.segments.all { entry ->
            val segment = segments[entry.key] ?: return
            val minLength = entry.value.minLength ?: 0f
            val maxLength = entry.value.maxLength ?: Float.MAX_VALUE
            segment.length in minLength..maxLength
        }

        val areAnglesValid = constraint.angles.all { entry ->
            val angle = angles[entry.key] ?: return
            val minDegree = entry.value.minDegree ?: 0f
            val maxDegree = entry.value.maxDegree ?: Float.MAX_VALUE
            angle.degree in minDegree..maxDegree
        }

        _uiState.update {
            it.copy(completed = areSegmentsValid && areAnglesValid)
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

    private fun onCompletionImageCaptured(path: String) {
        _uiState.update {
            it.copy(completionImage = path)
        }
    }
}