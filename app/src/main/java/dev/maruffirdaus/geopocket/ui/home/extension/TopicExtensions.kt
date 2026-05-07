package dev.maruffirdaus.geopocket.ui.home.extension

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.Angle
import com.adamglin.phosphoricons.regular.LineVertical
import com.adamglin.phosphoricons.regular.Rectangle
import com.adamglin.phosphoricons.regular.Triangle
import dev.maruffirdaus.geopocket.domain.topic.Topic

fun Topic.toIcon(): ImageVector = when (this) {
    Topic.LINE -> PhosphorIcons.Regular.LineVertical
    Topic.ANGLE -> PhosphorIcons.Regular.Angle
    Topic.TRIANGLE -> PhosphorIcons.Regular.Triangle
    Topic.QUADRILATERAL -> PhosphorIcons.Regular.Rectangle
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Topic.toIconContainerShape(): Shape = when (this) {
    Topic.LINE -> MaterialShapes.Pill.toShape()
    Topic.ANGLE -> MaterialShapes.Fan.toShape()
    Topic.TRIANGLE -> MaterialShapes.Arrow.toShape()
    Topic.QUADRILATERAL -> MaterialShapes.Cookie4Sided.toShape()
}