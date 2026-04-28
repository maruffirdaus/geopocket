package dev.maruffirdaus.geopocket.ui.instructions.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import dev.maruffirdaus.geopocket.domain.topic.Subtopic
import dev.maruffirdaus.geopocket.domain.topic.constraint.AngleConstraint
import dev.maruffirdaus.geopocket.domain.topic.constraint.SegmentConstraint
import dev.maruffirdaus.geopocket.ui.common.extensions.midpoint
import dev.maruffirdaus.geopocket.ui.common.extensions.pointPositions
import dev.maruffirdaus.geopocket.ui.common.extensions.textPaint
import kotlinx.coroutines.delay

private const val POINT_HOLD_DURATION = 400L
private const val SEGMENT_DRAW_DURATION = 500
private const val SEGMENT_HOLD_DURATION = 200L
private const val REPLAY_PAUSE_DURATION = 1_200L
private const val LABEL_VISIBLE_THRESHOLD = 0.4f

@Composable
fun InstructionsAnimationCanvas(
    subtopic: Subtopic,
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
    val pointTargetPositions by remember(canvasSize) {
        derivedStateOf { subtopic.pointPositions(canvasSize) }
    }

    val placedPointPositions = remember { mutableStateListOf<Offset>() }
    val pointScales = remember { List(constraint.pointCount) { Animatable(0f) } }

    val completedSegments = remember { mutableStateListOf<Pair<Int, Int>>() }
    var animatingFromIndex by remember { mutableIntStateOf(-1) }
    var animatingToIndex by remember { mutableIntStateOf(-1) }
    val segmentDrawProgress = remember { Animatable(0f) }

    LaunchedEffect(canvasSize) {
        while (true) {
            placedPointPositions.clear()
            completedSegments.clear()
            animatingFromIndex = -1
            animatingToIndex = -1
            segmentDrawProgress.snapTo(0f)
            pointScales.forEach { it.snapTo(0f) }

            pointTargetPositions.forEachIndexed { pointIndex, pointPosition ->
                placedPointPositions.add(pointPosition)
                pointScales[pointIndex].animateTo(
                    targetValue = 1f,
                    animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium)
                )
                delay(POINT_HOLD_DURATION)

                if (pointIndex >= 1) {
                    animatingFromIndex = pointIndex - 1
                    animatingToIndex = pointIndex
                    segmentDrawProgress.snapTo(0f)
                    segmentDrawProgress.animateTo(
                        1f,
                        tween(SEGMENT_DRAW_DURATION, easing = EaseInOut)
                    )
                    completedSegments.add(pointIndex - 1 to pointIndex)
                    animatingFromIndex = -1
                    animatingToIndex = -1
                    delay(SEGMENT_HOLD_DURATION)
                }
            }

            if (constraint.closedShape && pointTargetPositions.size >= 3) {
                animatingFromIndex = pointTargetPositions.lastIndex
                animatingToIndex = 0
                segmentDrawProgress.snapTo(0f)
                segmentDrawProgress.animateTo(1f, tween(SEGMENT_DRAW_DURATION, easing = EaseInOut))
                completedSegments.add(pointTargetPositions.lastIndex to 0)
                animatingFromIndex = -1
                animatingToIndex = -1
            }

            delay(REPLAY_PAUSE_DURATION)
        }
    }

    Canvas(
        modifier = modifier
    ) {
        if (canvasSize != size) canvasSize = size

        completedSegments.forEach { (fromPointIndex, toPointIndex) ->
            val segmentStart = pointTargetPositions[fromPointIndex]
            val segmentEnd = pointTargetPositions[toPointIndex]
            val segmentMidpoint = segmentStart.midpoint(segmentEnd)
            val segmentId = "${'A' + fromPointIndex}${'A' + toPointIndex}"
            val segmentConstraint = constraint.segments[segmentId]
            val segmentLabel = segmentConstraint?.formatString()

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

        if (completedSegments.size >= 2) {
            pointTargetPositions.forEachIndexed { pointIndex, pointPosition ->
                val incomingSegment = completedSegments.firstOrNull { it.second == pointIndex }
                    ?: return@forEachIndexed
                val outgoingSegment = completedSegments.firstOrNull { it.first == pointIndex }
                    ?: return@forEachIndexed
                val angleId =
                    "${'A' + incomingSegment.first}${'A' + pointIndex}${'A' + outgoingSegment.second}"
                val angleConstraint = constraint.angles[angleId]
                val angleLabel = angleConstraint?.formatString()

                angleLabel?.let {
                    drawContext.canvas.nativeCanvas.drawText(
                        it,
                        pointPosition.x,
                        pointPosition.y - pointRadiusPx - pointHaloPaddingPx - labelOffsetAbovePx,
                        labelColor.textPaint(labelTextSizePx, bold = true)
                    )
                }
            }
        }

        if (animatingFromIndex >= 0 && animatingToIndex >= 0) {
            val segmentStart = pointTargetPositions[animatingFromIndex]
            val segmentEnd = pointTargetPositions[animatingToIndex]
            val currentSegmentEnd = Offset(
                segmentStart.x + (segmentEnd.x - segmentStart.x) * segmentDrawProgress.value,
                segmentStart.y + (segmentEnd.y - segmentStart.y) * segmentDrawProgress.value
            )

            drawLine(
                color = segmentColor,
                start = segmentStart,
                end = currentSegmentEnd,
                strokeWidth = segmentStrokeWidthPx
            )

            if (segmentDrawProgress.value > LABEL_VISIBLE_THRESHOLD) {
                val animatingSegmentMidpoint = segmentStart.midpoint(currentSegmentEnd)
                val segmentId = "${'A' + animatingFromIndex}${'A' + animatingToIndex}"
                val segmentConstraint = constraint.segments[segmentId]
                val segmentLabel = segmentConstraint?.formatString()

                segmentLabel?.let {
                    drawContext.canvas.nativeCanvas.drawText(
                        it,
                        animatingSegmentMidpoint.x,
                        animatingSegmentMidpoint.y - labelOffsetAbovePx,
                        labelColor.textPaint(labelTextSizePx, bold = true)
                    )
                }
            }
        }

        placedPointPositions.forEachIndexed { pointIndex, pointPosition ->
            val scaledRadius = pointRadiusPx * pointScales[pointIndex].value
            val textPaint = pointLabelColor.textPaint(pointLabelTextSizePx, bold = true)
            val fontMetrics = textPaint.fontMetrics
            val centerY = pointPosition.y - (fontMetrics.ascent + fontMetrics.descent) / 2

            drawCircle(
                color = pointColor.copy(alpha = 0.38f),
                radius = scaledRadius + pointHaloPaddingPx,
                center = pointPosition
            )
            drawCircle(
                color = pointColor,
                radius = scaledRadius,
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

private fun SegmentConstraint.formatString(): String? {
    val convertedMinLength = minLength?.times(100)?.toInt()
    val convertedMaxLength = maxLength?.times(100)?.toInt()

    return when {
        minLength != null && maxLength != null && minLength == maxLength -> "$convertedMinLength cm"
        minLength != null && maxLength != null -> "$convertedMinLength - $convertedMaxLength cm"
        minLength != null -> "$convertedMinLength cm"
        maxLength != null -> "$convertedMaxLength cm"
        else -> null
    }
}

private fun AngleConstraint.formatString(): String? {
    return when {
        minDegree != null && maxDegree != null && minDegree == maxDegree -> "${minDegree.toInt()}°"
        minDegree != null && maxDegree != null -> "${minDegree.toInt()}° - ${maxDegree.toInt()}°"
        minDegree != null -> "${minDegree.toInt()}°"
        maxDegree != null -> "${maxDegree.toInt()}°"
        else -> null
    }
}