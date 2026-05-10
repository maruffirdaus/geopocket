package dev.maruffirdaus.geopocket.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.materialkolor.hct.Hct
import com.materialkolor.palettes.TonalPalette

val SeedColor = Color(0xFF2563EB)

val LineBaseColor = Color(0xFFBDBDBD)
val AngleBaseColor = Color(0xFF42A5F5)
val TriangleBaseColor = Color(0xFF66BB6A)
val QuadrilateralBaseColor = Color(0xFFFFCA28)

val FailBaseColor = Color(0xFFFF7043)

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
    val fail: ExtendedColorRoles
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
        ),
        fail = ExtendedColorRoles(
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

const val DisabledAlpha = 0.38f

fun Color.applyDisabledAlpha(enabled: Boolean): Color =
    copy(alpha = if (enabled) 1f else DisabledAlpha)