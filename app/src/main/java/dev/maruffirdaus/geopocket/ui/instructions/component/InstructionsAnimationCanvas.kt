package dev.maruffirdaus.geopocket.ui.instructions.component

import android.graphics.Paint
import android.graphics.Typeface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import dev.maruffirdaus.geopocket.domain.topic.Subtopic
import dev.maruffirdaus.geopocket.domain.topic.constraint.AngleConstraint
import dev.maruffirdaus.geopocket.domain.topic.constraint.SegmentConstraint
import kotlinx.coroutines.delay

private const val POINT_HOLD_DURATION = 400L
private const val SEGMENT_DRAW_DURATION = 500
private const val SEGMENT_HOLD_DURATION = 200L
private const val REPLAY_PAUSE_DURATION = 1_200L
private const val LABEL_VISIBLE_THRESHOLD = 0.4f
private const val SHAPE_RADIUS_FRACTION = 0.35f

@Composable
fun InstructionsAnimationCanvas(
    subtopic: Subtopic,
    modifier: Modifier = Modifier,
    pointColor: Color = MaterialTheme.colorScheme.primary,
    pointLabelColor: Color = MaterialTheme.colorScheme.onPrimary,
    segmentColor: Color = MaterialTheme.colorScheme.primary,
    labelColor: Color = MaterialTheme.colorScheme.onSurface,
    pointRadius: Dp = 16.dp,
    pointHaloPadding: Dp = 8.dp,
    pointLabelTextSize: TextUnit = MaterialTheme.typography.titleMedium.fontSize,
    labelTextSize: TextUnit = MaterialTheme.typography.labelLarge.fontSize,
    labelOffsetAbove: Dp = 8.dp,
    segmentStrokeWidth: Dp = 3.dp
) {
    val density = LocalDensity.current

    val constraint = subtopic.constraint

    val pointRadiusPx = with(density) { pointRadius.toPx() }
    val pointHaloPaddingPx = with(density) { pointHaloPadding.toPx() }
    val pointLabelTextSizePx = with(density) { pointLabelTextSize.toPx() }
    val labelTextSizePx = with(density) { labelTextSize.toPx() }
    val labelOffsetAbovePx = with(density) { labelOffsetAbove.toPx() }
    val segmentStrokeWidthPx = with(density) { segmentStrokeWidth.toPx() }

    val placedPointPositions = remember { mutableStateListOf<Offset>() }
    val completedSegments = remember { mutableStateListOf<Pair<Int, Int>>() }
    var animatingFromIndex by remember { mutableIntStateOf(-1) }
    var animatingToIndex by remember { mutableIntStateOf(-1) }
    val segmentDrawProgress = remember { Animatable(0f) }
    val pointScales = remember { List(constraint.pointCount) { Animatable(0f) } }
    var canvasSize by remember { mutableStateOf(Size(400f, 400f)) }
    val pointTargetPositions by remember(canvasSize) {
        derivedStateOf { subtopic.pointPositions(canvasSize) }
    }

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

        completedSegments.forEachIndexed { segmentIndex, (fromPointIndex, toPointIndex) ->
            val segmentStart = pointTargetPositions[fromPointIndex]
            val segmentEnd = pointTargetPositions[toPointIndex]
            val segmentMidpoint = segmentStart.midpoint(segmentEnd)
            val segmentConstraint = constraint.segments.getOrNull(segmentIndex)
            val lengthLabel = segmentConstraint?.formatString()

            drawLine(
                color = segmentColor,
                start = segmentStart,
                end = segmentEnd,
                strokeWidth = segmentStrokeWidthPx
            )

            lengthLabel?.let {
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
                completedSegments.firstOrNull { it.second == pointIndex } ?: return@forEachIndexed
                completedSegments.firstOrNull { it.first == pointIndex } ?: return@forEachIndexed
                val angleConstraint =
                    constraint.angles.getOrNull(pointIndex - 1) ?: constraint.angles.last()
                val angleLabel = angleConstraint.formatString()

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
                val segmentConstraint = constraint.segments.getOrNull(completedSegments.size)
                val lengthLabel = segmentConstraint?.formatString()

                lengthLabel?.let {
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

private fun Subtopic.pointPositions(canvasSize: Size): List<Offset> {
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

private fun Offset.midpoint(other: Offset) = Offset((x + other.x) / 2f, (y + other.y) / 2f)

private fun Color.textPaint(textSizePx: Float, bold: Boolean = false) = Paint().apply {
    color = this@textPaint.toArgb()
    textSize = textSizePx
    textAlign = Paint.Align.CENTER
    typeface = if (bold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
    isAntiAlias = true
}

private fun SegmentConstraint.formatString(): String? {
    return when {
        minLength != null && maxLength != null && minLength == maxLength -> {
            "%.0f cm".format(minLength * 100)
        }

        minLength != null && maxLength != null -> {
            "%.0f - %.0f cm".format(minLength * 100, maxLength * 100)
        }

        minLength != null -> "%.0f cm".format(minLength * 100)
        maxLength != null -> "%.0f cm".format(maxLength * 100)
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