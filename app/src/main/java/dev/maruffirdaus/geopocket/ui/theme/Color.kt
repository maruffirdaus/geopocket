package dev.maruffirdaus.geopocket.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.materialkolor.hct.Hct
import com.materialkolor.palettes.TonalPalette

val SeedColor = Color(0xFF2563EB)
val LineBaseColor = Color(0xFF1D9E75)
val AngleBaseColor = Color(0xFFBA7517)
val TriangleBaseColor = Color(0xFF7F77DD)
val QuadrilateralBaseColor = Color(0xFF639922)

@Immutable
data class ExtendedColorRoles(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color,
)

@Immutable
data class AppExtendedColors(
    val line: ExtendedColorRoles,
    val angle: ExtendedColorRoles,
    val triangle: ExtendedColorRoles,
    val quadrilateral: ExtendedColorRoles,
)

val LocalExtendedColors = staticCompositionLocalOf {
    AppExtendedColors(
        line = ExtendedColorRoles(
            Color.Unspecified,
            Color.Unspecified,
            Color.Unspecified,
            Color.Unspecified
        ),
        angle = ExtendedColorRoles(
            Color.Unspecified,
            Color.Unspecified,
            Color.Unspecified,
            Color.Unspecified
        ),
        triangle = ExtendedColorRoles(
            Color.Unspecified,
            Color.Unspecified,
            Color.Unspecified,
            Color.Unspecified
        ),
        quadrilateral = ExtendedColorRoles(
            Color.Unspecified,
            Color.Unspecified,
            Color.Unspecified,
            Color.Unspecified
        )
    )
}

fun Color.toTonalPalette(): TonalPalette {
    val argb = this.toArgb()
    val hct = Hct.fromInt(argb)
    return TonalPalette.fromHueAndChroma(hct.hue, hct.chroma)
}

fun extendedColorRoles(baseColor: Color, isDark: Boolean): ExtendedColorRoles {
    val palette = baseColor.toTonalPalette()
    return if (isDark) {
        ExtendedColorRoles(
            color = Color(palette.tone(80)),
            onColor = Color(palette.tone(20)),
            colorContainer = Color(palette.tone(30)),
            onColorContainer = Color(palette.tone(90)),
        )
    } else {
        ExtendedColorRoles(
            color = Color(palette.tone(40)),
            onColor = Color(palette.tone(100)),
            colorContainer = Color(palette.tone(90)),
            onColorContainer = Color(palette.tone(10)),
        )
    }
}

const val DisabledContentAlpha = 0.38f

fun Color.applyDisabledAlpha(enabled: Boolean): Color =
    copy(alpha = if (enabled) 1f else DisabledContentAlpha)