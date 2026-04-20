package dev.maruffirdaus.geopocket.ui.home.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.Angle
import com.adamglin.phosphoricons.regular.LineVertical
import com.adamglin.phosphoricons.regular.Rectangle
import com.adamglin.phosphoricons.regular.Triangle
import dev.maruffirdaus.geopocket.ui.common.model.ARPlacingMode

enum class HomeItem(
    val title: String,
    val icon: ImageVector,
    val description: String,
    val status: HomeItemStatus
) {
    LINE(
        "Konsep Garis",
        PhosphorIcons.Regular.LineVertical,
        "Dasar bentuk geometri yang terbentuk dari kumpulan titik yang tersusun lurus.",
        HomeItemStatus.COMPLETED
    ),
    ANGLE(
        "Konsep Sudut",
        PhosphorIcons.Regular.Angle,
        "Bentuk yang terjadi ketika dua garis bertemu pada satu titik.",
        HomeItemStatus.IN_PROGRESS
    ),
    TRIANGLE(
        "Konsep Segitiga",
        PhosphorIcons.Regular.Triangle,
        "Bangun datar dengan tiga sisi dan tiga sudut.",
        HomeItemStatus.LOCKED
    ),
    QUADRILATERAL(
        "Konsep Segiempat",
        PhosphorIcons.Regular.Rectangle,
        "Bangun datar yang memiliki empat sisi dan empat sudut.",
        HomeItemStatus.LOCKED
    );

    fun toARPlacingMode(): ARPlacingMode {
        return ARPlacingMode.entries[this.ordinal]
    }
}