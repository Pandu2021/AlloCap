package com.pandu.allocap.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = DeepSpruce,
    secondary = WarmTerracotta,
    background = PaleSageMint,
    surface = PaleSageMint,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = CharcoalSlate,
    onSurface = CharcoalSlate,
)

@Composable
fun AlloCapTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
