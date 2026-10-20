package com.example.composeapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Material 3 roles exported from the Stitch design. The design is light-only and brand-driven,
// so there is no dark scheme and no dynamic color.
private val FreelyColorScheme = lightColorScheme(
    primary = Color(0xFFB42901),
    onPrimary = Color.White,
    primaryContainer = SunnyCoral,
    onPrimaryContainer = Color(0xFF5B1000),
    inversePrimary = Color(0xFFFFB4A2),
    secondary = Color(0xFF7E5700),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEB300),
    onSecondaryContainer = Color(0xFF6A4800),
    tertiary = Color(0xFF006C45),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF00A96D),
    onTertiaryContainer = Color(0xFF00341F),
    background = Color(0xFFFBF9F5),
    onBackground = Color(0xFF1B1C1A),
    surface = Color(0xFFFBF9F5),
    onSurface = Color(0xFF1B1C1A),
    surfaceVariant = Color(0xFFE4E2DE),
    onSurfaceVariant = Color(0xFF5A413A),
    surfaceTint = Color(0xFFB42901),
    inverseSurface = Color(0xFF30312E),
    inverseOnSurface = Color(0xFFF2F0ED),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF93000A),
    outline = Color(0xFF8F7069),
    outlineVariant = Color(0xFFE3BEB6),
    surfaceBright = Color(0xFFFBF9F5),
    surfaceDim = Color(0xFFDBDAD6),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF5F3EF),
    surfaceContainer = Color(0xFFEFEEEA),
    surfaceContainerHigh = Color(0xFFEAE8E4),
    surfaceContainerHighest = Color(0xFFE4E2DE),
)

// Stitch radii: DEFAULT 1rem, lg 2rem, xl 3rem.
private val FreelyShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(32.dp),
    extraLarge = RoundedCornerShape(48.dp),
)

/** Gradients that recur across the Freely screens. */
object FreelyGradients {
    /** Coral to marigold: logo, "Say Hello", unread badges. */
    val Sunrise = Brush.horizontalGradient(listOf(SunnyCoral, GoldenMarigold))

    /** Coral to deep red: primary calls to action and the send button. */
    val Ember = Brush.horizontalGradient(listOf(SunnyCoral, Color(0xFFB42901)))

    /** Story-style ring around avatars with something new to see. */
    val Ring = Brush.linearGradient(listOf(SunnyCoral, GoldenMarigold, ZestyMint))

    val DailyPing = Brush.horizontalGradient(listOf(GoldenMarigold, SunnyCoral, Color(0xFFD9481F)))

    val OutgoingBubble = Brush.horizontalGradient(listOf(Color(0xFFFF6A3D), SunnyCoral))

    /** Soft peach wash behind hero and highlight cards. */
    val PeachGlow = Brush.linearGradient(listOf(PeachCreme, Color(0xFFFFE3D3), WarmSand))

    /** Keeps white text readable over the bottom of a photo. */
    val PhotoScrim = Brush.verticalGradient(
        0f to Color.Transparent,
        0.35f to Color.Transparent,
        1f to DeepEspresso.copy(alpha = 0.94f),
    )
}

@Composable
fun FreelyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FreelyColorScheme,
        typography = FreelyTypography,
        shapes = FreelyShapes,
        content = content
    )
}
