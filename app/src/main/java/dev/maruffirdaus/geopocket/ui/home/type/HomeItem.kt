package dev.maruffirdaus.geopocket.ui.home.type

import androidx.annotation.DrawableRes
import dev.maruffirdaus.geopocket.R

enum class HomeItem(
    val title: String,
    @param:DrawableRes val icon: Int
) {
    LINE("Concept of Line", R.drawable.ic_line_vertical),
    TRIANGLE("Concept of Triangle", R.drawable.ic_triangle),
}