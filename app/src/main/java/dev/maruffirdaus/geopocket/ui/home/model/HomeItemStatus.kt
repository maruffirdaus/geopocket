package dev.maruffirdaus.geopocket.ui.home.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.Flag
import com.adamglin.phosphoricons.regular.Hourglass
import com.adamglin.phosphoricons.regular.Lock

enum class HomeItemStatus(
    val title: String,
    val icon: ImageVector,
) {
    LOCKED("Terkunci", PhosphorIcons.Regular.Lock),
    IN_PROGRESS("Berjalan", PhosphorIcons.Regular.Hourglass),
    COMPLETED("Selesai", PhosphorIcons.Regular.Flag)
}