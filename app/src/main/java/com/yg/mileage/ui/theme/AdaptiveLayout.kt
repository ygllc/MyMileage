package com.yg.mileage.ui.theme

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Applies responsive horizontal padding and max width while keeping existing visuals unchanged.
 */
fun Modifier.adaptiveContent(widthSizeClass: WindowWidthSizeClass): Modifier {
    val horizontal = when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> 16.dp
        WindowWidthSizeClass.Medium -> 24.dp
        WindowWidthSizeClass.Expanded -> 32.dp
        else -> 16.dp
    }
    val maxWidth = when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> 900.dp
        WindowWidthSizeClass.Medium -> 1040.dp
        WindowWidthSizeClass.Expanded -> 1280.dp
        else -> 900.dp
    }
    return this
        .fillMaxWidth()
        .padding(horizontal = horizontal)
        .widthIn(max = maxWidth)
}
