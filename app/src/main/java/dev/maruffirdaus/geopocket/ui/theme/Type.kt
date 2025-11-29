package dev.maruffirdaus.geopocket.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import dev.maruffirdaus.geopocket.R

val plusJakartaSansFontFamily = FontFamily(
    Font(R.font.plus_jakarta_sans_extra_light, FontWeight.ExtraLight),
    Font(R.font.plus_jakarta_sans_extra_light_italic, FontWeight.ExtraLight, FontStyle.Italic),
    Font(R.font.plus_jakarta_sans_light, FontWeight.Light),
    Font(R.font.plus_jakarta_sans_light_italic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.plus_jakarta_sans_regular, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.plus_jakarta_sans_medium, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_medium_italic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.plus_jakarta_sans_semi_bold, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_semi_bold_italic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold),
    Font(R.font.plus_jakarta_sans_bold_italic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.plus_jakarta_sans_extra_bold, FontWeight.ExtraBold),
    Font(R.font.plus_jakarta_sans_extra_bold_italic, FontWeight.ExtraBold, FontStyle.Italic)
)

private val typography = Typography()

val Typography = Typography(
    displayLarge = typography.displayLarge.copy(fontFamily = plusJakartaSansFontFamily),
    displayMedium = typography.displayMedium.copy(fontFamily = plusJakartaSansFontFamily),
    displaySmall = typography.displaySmall.copy(fontFamily = plusJakartaSansFontFamily),
    headlineLarge = typography.headlineLarge.copy(fontFamily = plusJakartaSansFontFamily),
    headlineMedium = typography.headlineMedium.copy(fontFamily = plusJakartaSansFontFamily),
    headlineSmall = typography.headlineSmall.copy(fontFamily = plusJakartaSansFontFamily),
    titleLarge = typography.titleLarge.copy(fontFamily = plusJakartaSansFontFamily),
    titleMedium = typography.titleMedium.copy(fontFamily = plusJakartaSansFontFamily),
    titleSmall = typography.titleSmall.copy(fontFamily = plusJakartaSansFontFamily),
    bodyLarge = typography.bodyLarge.copy(fontFamily = plusJakartaSansFontFamily),
    bodyMedium = typography.bodyMedium.copy(fontFamily = plusJakartaSansFontFamily),
    bodySmall = typography.bodySmall.copy(fontFamily = plusJakartaSansFontFamily),
    labelLarge = typography.labelLarge.copy(fontFamily = plusJakartaSansFontFamily),
    labelMedium = typography.labelMedium.copy(fontFamily = plusJakartaSansFontFamily),
    labelSmall = typography.labelSmall.copy(fontFamily = plusJakartaSansFontFamily)
)