package com.dawidkubica.physio.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.dawidkubica.physio.models.ThemeMode
import androidx.compose.material3.MaterialTheme as Material3Theme

private val DarkColorScheme = darkColorScheme(
    primary = newDarkPrimary,
    secondary = newDarkSecondary,
    tertiary = newDarkTertiary,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = OnPrimaryDark,
    onSecondary = OnSecondaryDark
)

private val LightColorScheme = lightColorScheme(
    //primary = Purple40,
    primary = newPrimary,
    //secondary = PurpleGrey40,
    secondary = newSecondary,
    //tertiary = PurpleGrey80,
    tertiary = newTertiary,
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color(0xFF000000),
    onSecondary = Color(0xFF000000)
)

@Composable
fun PhysioBarTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val isDarkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (isDarkTheme) DarkColorScheme else LightColorScheme

    Material3Theme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = Shapes,
        content = content
    )

}

@Composable
fun PhysioTheme(content: @Composable() () -> Unit) {

    Material3Theme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = Shapes,
        content = content
    )
}
