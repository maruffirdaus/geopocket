package dev.maruffirdaus.geopocket.ui.quiz.questions.component

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import dev.maruffirdaus.geopocket.domain.topic.Subtopic
import dev.maruffirdaus.geopocket.domain.topic.result.AngleResult
import dev.maruffirdaus.geopocket.domain.topic.result.SegmentResult
import dev.maruffirdaus.geopocket.ui.common.extension.midpoint
import dev.maruffirdaus.geopocket.ui.common.extension.pointPositions
import dev.maruffirdaus.geopocket.ui.common.extension.textPaint

@Composable
fun ShapeCanvas(
    subtopic: Subtopic,
    segments: Map<String, SegmentResult>,
    angles: Map<String, AngleResult>,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    val constraint = subtopic.constraint

    val pointColor = MaterialTheme.colorScheme.primary
    val pointLabelColor = MaterialTheme.colorScheme.onPrimary
    val segmentColor = MaterialTheme.colorScheme.primary
    val labelColor = MaterialTheme.colorScheme.onSurface

    val pointRadiusPx = with(density) { 16.dp.toPx() }
    val pointHaloPaddingPx = with(density) { 8.dp.toPx() }
    val pointLabelTextSizePx = with(density) { MaterialTheme.typography.titleMedium.fontSize.toPx() }
    val labelTextSizePx = with(density) { MaterialTheme.typography.labelLarge.fontSize.toPx() }
    val labelOffsetAbovePx = with(density) { 8.dp.toPx() }
    val segmentStrokeWidthPx = with(density) { 3.dp.toPx() }

    var canvasSize by remember { mutableStateOf(Size(400f, 400f)) }
    val pointPositions by remember(canvasSize) {
        derivedStateOf { subtopic.pointPositions(canvasSize) }
    }

    val connections = buildList {
        repeat(constraint.pointCount - 1) { index ->
            add(index to index + 1)
        }
        if (constraint.closedShape) add(constraint.pointCount - 1 to 0)
    }

    Canvas(
        modifier = modifier
    ) {
        if (canvasSize != size) canvasSize = size

        connections.forEach { (fromPointIndex, toPointIndex) ->
            val segmentStart = pointPositions[fromPointIndex]
            val segmentEnd = pointPositions[toPointIndex]
            val segmentMidpoint = segmentStart.midpoint(segmentEnd)
            val segmentId = "${'A' + fromPointIndex}${'A' + toPointIndex}"
            val segmentResult = segments[segmentId]
            val segmentLabel = segmentResult?.formatString()

            drawLine(
                color = segmentColor,
                start = segmentStart,
                end = segmentEnd,
                strokeWidth = segmentStrokeWidthPx
            )

            segmentLabel?.let {
                drawContext.canvas.nativeCanvas.drawText(
                    it,
                    segmentMidpoint.x,
                    segmentMidpoint.y - labelOffsetAbovePx,
                    labelColor.textPaint(labelTextSizePx, bold = true)
                )
            }
        }

        pointPositions.forEachIndexed { pointIndex, pointPosition ->
            val incomingSegment =
                connections.firstOrNull { it.second == pointIndex } ?: return@forEachIndexed
            val outgoingSegment = connections.firstOrNull { it.first == pointIndex }
                ?: return@forEachIndexed
            val angleId =
                "${'A' + incomingSegment.first}${'A' + pointIndex}${'A' + outgoingSegment.second}"
            val angleResult = angles[angleId]
            val angleLabel = angleResult?.formatString()

            angleLabel?.let {
                drawContext.canvas.nativeCanvas.drawText(
                    it,
                    pointPosition.x,
                    pointPosition.y - pointRadiusPx - pointHaloPaddingPx - labelOffsetAbovePx,
                    labelColor.textPaint(labelTextSizePx, bold = true)
                )
            }
        }

        pointPositions.forEachIndexed { pointIndex, pointPosition ->
            val textPaint = pointLabelColor.textPaint(pointLabelTextSizePx, bold = true)
            val fontMetrics = textPaint.fontMetrics
            val centerY = pointPosition.y - (fontMetrics.ascent + fontMetrics.descent) / 2

            drawCircle(
                color = pointColor.copy(alpha = 0.38f),
                radius = pointRadiusPx + pointHaloPaddingPx,
                center = pointPosition
            )
            drawCircle(
                color = pointColor,
                radius = pointRadiusPx,
                center = pointPosition
            )
            drawContext.canvas.nativeCanvas.drawText(
                ('A' + pointIndex).toString(),
                pointPosition.x,
                centerY,
                textPaint
            )
        }
    }
}

private fun SegmentResult.formatString(): String = "${(length * 100).toInt()} cm"

private fun AngleResult.formatString(): String = "${degree.toInt()}°"