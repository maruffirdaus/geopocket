package dev.maruffirdaus.geopocket.ui.common.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.Clock
import com.adamglin.phosphoricons.regular.GraduationCap
import com.adamglin.phosphoricons.regular.Lock

enum class TopicStatus(
    val title: String,
    val icon: ImageVector,
) {
    LOCKED("Terkunci", PhosphorIcons.Regular.Lock),
    NOT_PASSED("Belum lulus", PhosphorIcons.Regular.Clock),
    PASSED("Lulus", PhosphorIcons.Regular.GraduationCap)
}