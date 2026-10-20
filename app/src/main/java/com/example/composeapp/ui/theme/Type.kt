package com.example.composeapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.composeapp.R

val PlusJakartaSans = FontFamily(
    Font(R.font.plus_jakarta_sans_regular, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_medium, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold),
    Font(R.font.plus_jakarta_sans_extrabold, FontWeight.ExtraBold),
)

private fun loopStyle(size: Int, lineHeight: Int, weight: FontWeight, letterSpacingEm: Double = 0.0) =
    TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = weight,
        fontSize = size.sp,
        lineHeight = lineHeight.sp,
        letterSpacing = letterSpacingEm.em,
    )

// Stitch type scale mapped onto Material 3 slots (Stitch name in comments).
val FreelyTypography = Typography(
    displayLarge = loopStyle(36, 44, FontWeight.ExtraBold, -0.03),      // display
    displayMedium = loopStyle(36, 44, FontWeight.ExtraBold, -0.03),     // display
    displaySmall = loopStyle(30, 38, FontWeight.ExtraBold, -0.025),     // display-mobile
    headlineLarge = loopStyle(28, 34, FontWeight.ExtraBold, -0.02),     // headline-lg
    headlineMedium = loopStyle(24, 30, FontWeight.ExtraBold, -0.02),    // headline-lg-mobile
    headlineSmall = loopStyle(22, 28, FontWeight.Bold, -0.015),         // headline-md
    titleLarge = loopStyle(18, 24, FontWeight.Bold, -0.01),             // headline-sm
    titleMedium = loopStyle(16, 22, FontWeight.Bold),                   // title-md
    titleSmall = loopStyle(14, 20, FontWeight.Bold, 0.01),              // label-lg
    bodyLarge = loopStyle(16, 24, FontWeight.Medium),                   // body-lg
    bodyMedium = loopStyle(14, 20, FontWeight.Medium),                  // body-md
    bodySmall = loopStyle(13, 18, FontWeight.Normal),                   // body-sm
    labelLarge = loopStyle(14, 20, FontWeight.Bold, 0.01),              // label-lg
    labelMedium = loopStyle(12, 16, FontWeight.Bold, 0.02),             // label-md
    labelSmall = loopStyle(11, 14, FontWeight.SemiBold, 0.03),          // label-sm
)
