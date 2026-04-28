package dev.maruffirdaus.geopocket.ui.common.extensions

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import dev.maruffirdaus.geopocket.domain.topic.Subtopic

private const val SHAPE_RADIUS_FRACTION = 0.35f

fun Subtopic.pointPositions(canvasSize: Size): List<Offset> {
    val centerX = canvasSize.width / 2f
    val centerY = canvasSize.height / 2f
    val shapeRadius = minOf(canvasSize.width, canvasSize.height) * SHAPE_RADIUS_FRACTION

    return when (this) {
        Subtopic.LINE_SEGMENT -> listOf(
            Offset(centerX - shapeRadius, centerY),
            Offset(centerX + shapeRadius, centerY)
        )

        Subtopic.ANGLE_ACUTE -> listOf(
            Offset(centerX + shapeRadius, centerY - shapeRadius),
            Offset(centerX - shapeRadius, centerY + shapeRadius),
            Offset(centerX + shapeRadius, centerY + shapeRadius)
        )

        Subtopic.ANGLE_RIGHT -> listOf(
            Offset(centerX - shapeRadius, centerY - shapeRadius),
            Offset(centerX - shapeRadius, centerY + shapeRadius),
            Offset(centerX + shapeRadius, centerY + shapeRadius)
        )

        Subtopic.ANGLE_OBTUSE -> listOf(
            Offset(centerX - shapeRadius, centerY - shapeRadius),
            Offset(centerX - shapeRadius / 2f, centerY + shapeRadius / 2f),
            Offset(centerX + shapeRadius, centerY + shapeRadius)
        )

        Subtopic.TRIANGLE_EQUILATERAL -> {
            listOf(
                Offset(centerX, centerY - shapeRadius * 0.75f),
                Offset(centerX - shapeRadius, centerY + shapeRadius * 0.75f),
                Offset(centerX + shapeRadius, centerY + shapeRadius * 0.75f)
            )
        }

        Subtopic.TRIANGLE_ISOSCELES -> listOf(
            Offset(centerX, centerY - shapeRadius),
            Offset(centerX - shapeRadius, centerY + shapeRadius),
            Offset(centerX + shapeRadius, centerY + shapeRadius)
        )

        Subtopic.TRIANGLE_SCALENE -> {
            listOf(
                Offset(centerX, centerY - shapeRadius),
                Offset(centerX - shapeRadius, centerY + shapeRadius / 2f),
                Offset(centerX + shapeRadius, centerY + shapeRadius)
            )
        }

        Subtopic.QUADRILATERAL_SQUARE -> listOf(
            Offset(centerX - shapeRadius, centerY - shapeRadius),
            Offset(centerX - shapeRadius, centerY + shapeRadius),
            Offset(centerX + shapeRadius, centerY + shapeRadius),
            Offset(centerX + shapeRadius, centerY - shapeRadius)
        )

        Subtopic.QUADRILATERAL_RECTANGLE -> listOf(
            Offset(centerX - shapeRadius, centerY - shapeRadius * 0.75f),
            Offset(centerX - shapeRadius, centerY + shapeRadius * 0.75f),
            Offset(centerX + shapeRadius, centerY + shapeRadius * 0.75f),
            Offset(centerX + shapeRadius, centerY - shapeRadius * 0.75f)
        )
    }
}