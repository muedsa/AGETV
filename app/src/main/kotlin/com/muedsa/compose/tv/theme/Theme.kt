package com.muedsa.compose.tv.theme

import androidx.compose.runtime.Composable
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

@OptIn(ExperimentalTvMaterial3Api::class)
private val DarkColorScheme = darkColorScheme(
//    background = background,
//    onBackground = onBackground,
//    surface = surface,
//    onSurface = onSurface,
//    surfaceVariant = surfaceVariant,
//    onSurfaceVariant = onSurfaceVariant
)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}