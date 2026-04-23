package dev.maruffirdaus.geopocket.ui.ar.model

import dev.romainguy.kotlin.math.Quaternion
import dev.romainguy.kotlin.math.degrees
import dev.romainguy.kotlin.math.dot
import dev.romainguy.kotlin.math.length
import dev.romainguy.kotlin.math.normalize
import io.github.sceneview.math.Position
import kotlin.math.acos
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class PointNodeState(
    val id: String = Uuid.random().toString(),
    val worldPosition: Position = Position(),
    val quaternion: Quaternion = Quaternion(),
    val label: String = "A",
    val connectedSegmentIds: List<String> = listOf()
) {
    fun angleBetween(start: PointNodeState, end: PointNodeState): Float {
        val posA = start.worldPosition
        val posB = worldPosition
        val posC = end.worldPosition

        val ba = posA - posB
        val bc = posC - posB

        val dotProduct = dot(ba, bc)
        val magnitudes = length(ba) * length(bc)

        val cosAngle = (dotProduct / magnitudes).coerceIn(-1f, 1f)

        return degrees(acos(cosAngle))
    }

    fun angleBisectorPosition(
        start: PointNodeState,
        end: PointNodeState,
        offset: Float = 0.05f
    ): Position {
        val posB = worldPosition

        val dirA =
            normalize(Position(start.worldPosition.x - posB.x, 0f, start.worldPosition.z - posB.z))
        val dirC =
            normalize(Position(end.worldPosition.x - posB.x, 0f, end.worldPosition.z - posB.z))

        val bisector = normalize(dirA + dirC)

        return Position(
            posB.x + bisector.x * offset,
            posB.y,
            posB.z + bisector.z * offset
        )
    }
}
