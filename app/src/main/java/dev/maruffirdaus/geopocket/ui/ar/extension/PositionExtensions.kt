package dev.maruffirdaus.geopocket.ui.ar.extension

import dev.romainguy.kotlin.math.degrees
import dev.romainguy.kotlin.math.dot
import dev.romainguy.kotlin.math.length
import dev.romainguy.kotlin.math.normalize
import io.github.sceneview.math.Position
import kotlin.math.acos

fun Position.angleBetween(startPos: Position, endPos: Position): Float {
    val ba = startPos - this
    val bc = endPos - this

    val dotProduct = dot(ba, bc)
    val magnitudes = length(ba) * length(bc)

    val cosAngle = (dotProduct / magnitudes).coerceIn(-1f, 1f)

    return degrees(acos(cosAngle))
}

fun Position.angleBisectorPosition(
    startPos: Position,
    endPos: Position,
    offset: Float = 0.05f
): Position {
    val dirA =
        normalize(Position(startPos.x - this.x, 0f, startPos.z - this.z))
    val dirC =
        normalize(Position(endPos.x - this.x, 0f, endPos.z - this.z))

    val bisector = normalize(dirA + dirC)

    return Position(
        this.x + bisector.x * offset,
        this.y,
        this.z + bisector.z * offset
    )
}