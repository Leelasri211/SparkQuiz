package com.leelasri.sparkquiz.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Two-face type system: FontFamily.Default carries the question/body copy; FontFamily.Monospace
 * is reserved for anything "data" — counters, scores, badges, buttons — for a developer-console
 * feel that matches the quiz's Android-trivia subject matter. No new font files/dependencies —
 * both are system-bundled.
 */
val Typography = Typography(
    headlineMedium = TextStyle( // Results title
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 30.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.4).sp
    ),
    headlineSmall = TextStyle( // Question text
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 25.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.2).sp
    ),
    titleLarge = TextStyle( // Big stat numbers on the Results screen
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    bodyLarge = TextStyle( // Option text
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle( // Results subtitle, splash error text
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 21.sp
    ),
    bodySmall = TextStyle( // "N skipped" note
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    labelLarge = TextStyle( // Buttons, question eyebrow
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        letterSpacing = 0.5.sp
    ),
    labelMedium = TextStyle( // Stat card labels
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 0.8.sp
    ),
    labelSmall = TextStyle( // Option A/B/C/D badge letters
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
    )
)