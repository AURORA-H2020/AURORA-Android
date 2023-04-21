package eu.inscico.aurora_app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorPalette = darkColorScheme(
    primary = primaryLight,
    secondary = primaryDark,
    background = backgroundColorDark,
    onSecondary = whiteMediumEmphasis,
    surface = surfacesDarkTheme,
    onBackground = outlineVariantDark,
    error = error,
    tertiary = tertiary,
    outlineVariant = outlineVariantLight,
)

private val LightColorPalette = lightColorScheme(
    primary = primary,
    secondary = primaryLight,
    background = backgroundColorLight,
    onSecondary = blackMediumEmphasis,
    surface = surfacesLightTheme,
    onBackground = outlineVariantLight,
    error = error,
    tertiary = tertiary,
    outlineVariant = outlineVariantDark

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun AURORAEnergyTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorscheme = if (darkTheme) {
        LightColorPalette
        // TODO:  
    } else {
        LightColorPalette
    }

    val colorScheme = colorscheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }


    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
        content = content
    )
}