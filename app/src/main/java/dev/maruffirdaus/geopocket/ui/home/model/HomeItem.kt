package dev.maruffirdaus.geopocket.ui.home.model

import androidx.annotation.DrawableRes
import dev.maruffirdaus.geopocket.R
import dev.maruffirdaus.geopocket.ui.common.model.ArPlacingMode

enum class HomeItem(
    val title: String,
    @param:DrawableRes val icon: Int
) {
    LINE("Concept of Line", R.drawable.ic_line_vertical),
    TRIANGLE("Concept of Triangle", R.drawable.ic_triangle),
    RECTANGLE("Concept of Rectangle", R.drawable.ic_rectangle);

    fun toArPlacingMode(): ArPlacingMode {
        return ArPlacingMode.entries[this.ordinal]
    }
}