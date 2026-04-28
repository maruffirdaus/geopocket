package dev.maruffirdaus.geopocket.ui.common.extensions

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

fun Color.textPaint(textSizePx: Float, bold: Boolean = false): Paint = Paint().apply {
    color = this@textPaint.toArgb()
    textSize = textSizePx
    textAlign = Paint.Align.CENTER
    typeface = if (bold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
    isAntiAlias = true
}