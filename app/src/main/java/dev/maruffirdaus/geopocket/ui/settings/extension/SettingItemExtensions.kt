package dev.maruffirdaus.geopocket.ui.settings.extension

import androidx.compose.ui.graphics.vector.ImageVector
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.ArrowClockwise
import com.adamglin.phosphoricons.regular.Crosshair
import com.adamglin.phosphoricons.regular.Info
import com.adamglin.phosphoricons.regular.Ruler
import dev.maruffirdaus.geopocket.domain.settings.SettingItem

fun SettingItem.toIcon(): ImageVector = when (this) {
    SettingItem.MeasurementAssist -> PhosphorIcons.Regular.Ruler
    SettingItem.SmoothInteraction -> PhosphorIcons.Regular.Crosshair
    SettingItem.ResetProgress -> PhosphorIcons.Regular.ArrowClockwise
    SettingItem.Licenses -> PhosphorIcons.Regular.Info
}