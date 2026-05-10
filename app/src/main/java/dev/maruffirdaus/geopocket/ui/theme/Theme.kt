package dev.maruffirdaus.geopocket.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.materialkolor.ktx.harmonize
import com.materialkolor.rememberDynamicColorScheme

@Composable
fun GeoPocketTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = rememberDynamicColorScheme(seedColor = SeedColor, isDark = darkTheme)
    val primary = colorScheme.primary

    val extendedColors = AppExtendedColors(
        line = extendedColorRoles(LineBaseColor.harmonize(primary), darkTheme),
        angle = extendedColorRoles(AngleBaseColor.harmonize(primary), darkTheme),
        triangle = extendedColorRoles(TriangleBaseColor.harmonize(primary), darkTheme),
        quadrilateral = extendedColorRoles(QuadrilateralBaseColor.harmonize(primary), darkTheme),
        fail = extendedColorRoles(FailBaseColor.harmonize(primary), darkTheme)
    )

    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}