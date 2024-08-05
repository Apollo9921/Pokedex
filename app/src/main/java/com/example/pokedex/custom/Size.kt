package com.example.pokedex.custom

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val small = 600.dp
val normal = 840.dp

@Composable
fun mediaQueryWidth(): Dp {
    return LocalContext.current.resources.displayMetrics.widthPixels.dp / LocalDensity.current.density
}
