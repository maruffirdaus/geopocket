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
    val connectedSegmentIds: Set<String> = setOf()
) {
    fun angleBetween(startPos: Position, endPos: Position): Float {
        val centerPos = worldPosition

        val ba = startPos - centerPos
        val bc = endPos - centerPos

        val dotProduct = dot(ba, bc)
        val magnitudes = length(ba) * length(bc)

        val cosAngle = (dotProduct / magnitudes).coerceIn(-1f, 1f)

        return degrees(acos(cosAngle))
    }

    fun angleBisectorPosition(
        startPos: Position,
        endPos: Position,
        offset: Float = 0.05f
    ): Position {
        val centerPos = worldPosition

        val dirA =
            normalize(Position(startPos.x - centerPos.x, 0f, startPos.z - centerPos.z))
        val dirC =
            normalize(Position(endPos.x - centerPos.x, 0f, endPos.z - centerPos.z))

        val bisector = normalize(dirA + dirC)

        return Position(
            centerPos.x + bisector.x * offset,
            centerPos.y,
            centerPos.z + bisector.z * offset
        )
    }
}
