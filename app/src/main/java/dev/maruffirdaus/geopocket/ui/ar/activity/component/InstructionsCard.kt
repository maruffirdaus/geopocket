package dev.maruffirdaus.geopocket.ui.ar.activity.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.maruffirdaus.geopocket.domain.topic.Subtopic
import dev.maruffirdaus.geopocket.domain.topic.constraint.AngleConstraint
import dev.maruffirdaus.geopocket.domain.topic.constraint.Constraint
import dev.maruffirdaus.geopocket.domain.topic.constraint.SegmentConstraint
import dev.maruffirdaus.geopocket.ui.ar.activity.extension.toHint
import dev.maruffirdaus.geopocket.ui.ar.activity.model.AngleNodeState
import dev.maruffirdaus.geopocket.ui.ar.activity.model.SegmentNodeState
import dev.maruffirdaus.geopocket.ui.common.component.PageIndicator
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme
import kotlinx.coroutines.launch
import kotlin.math.ceil

@Composable
fun InstructionsCard(
    segments: Map<String, SegmentNodeState>,
    angles: Map<String, AngleNodeState>,
    constraint: Constraint,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val segmentConstraints = constraint.segments.values.sortedBy { it.id }
        val angleConstraints = constraint.angles.values.sortedBy { it.id }
        val combinedConstraints = segmentConstraints + angleConstraints
        val size = ceil(combinedConstraints.size / 2f).toInt()
        val pagerState = rememberPagerState { size }

        if (size > 1) {
            PageIndicator(
                size = size,
                currentIndex = pagerState.currentPage,
                onClick = { index ->
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                repeat(2) { index ->
                    val currentIndex = pagerState.currentPage
                    when (val item = combinedConstraints.getOrNull(index + 2 * currentIndex)) {
                        is SegmentConstraint -> {
                            val segment = segments[item.id]
                            CheckboxItem(
                                text = item.toHint(),
                                checked = segment?.let {
                                    val minLength = item.minLength ?: 0f
                                    val maxLength = item.maxLength ?: Float.MAX_VALUE
                                    it.length in minLength..maxLength
                                } ?: false
                            )
                        }

                        is AngleConstraint -> {
                            val angle = angles[item.id]
                            CheckboxItem(
                                text = item.toHint(),
                                checked = angle?.let {
                                    val minDegree = item.minDegree ?: 0f
                                    val maxDegree = item.maxDegree ?: Float.MAX_VALUE
                                    angle.degree in minDegree..maxDegree
                                } ?: false
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckboxItem(
    text: String,
    checked: Boolean = false
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
@Preview
private fun InstructionsCardPreview() {
    GeoPocketTheme {
        InstructionsCard(
            segments = mapOf(),
            angles = mapOf(),
            constraint = Subtopic.QUADRILATERAL_RECTANGLE.constraint
        )
    }
}