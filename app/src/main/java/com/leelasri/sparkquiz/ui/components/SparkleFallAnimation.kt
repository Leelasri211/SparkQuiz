package com.leelasri.sparkquiz.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import kotlin.random.Random

private data class Sparkle(
    val startXFraction: Float,
    val startDelayFraction: Float,
    val baseRadiusDp: Float,
    val horizontalDriftDp: Float,
    val colorIndex: Int
)

@Composable
fun SparkleFallAnimation(modifier: Modifier = Modifier, sparkleCount: Int = 28) {
    val sparkles = remember {
        List(sparkleCount) {
            Sparkle(
                startXFraction = Random.nextFloat(),
                startDelayFraction = Random.nextFloat() * 0.5f,
                baseRadiusDp = Random.nextFloat() * 2.5f + 2.5f,
                horizontalDriftDp = Random.nextFloat() * 30f - 15f,
                colorIndex = Random.nextInt(3)
            )
        }
    }
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(1f, animationSpec = tween(durationMillis = 2600, easing = LinearEasing))
    }

    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        sparkles.forEach { sparkle ->
            val local = ((progress.value - sparkle.startDelayFraction) / (1f - sparkle.startDelayFraction))
                .coerceIn(0f, 1f)
            if (local <= 0f || local >= 1f) return@forEach

            val y = size.height * local
            val x = (size.width * sparkle.startXFraction) + (sparkle.horizontalDriftDp.dp.toPx() * local)
            val alpha = 1f - local
            val color = colors[sparkle.colorIndex]
            val coreRadius = sparkle.baseRadiusDp.dp.toPx()

            drawCircle(
                color = color.copy(alpha = alpha * 0.25f),
                radius = coreRadius * 2.2f,
                center = Offset(x, y)
            )
            drawCircle(
                color = color.copy(alpha = alpha),
                radius = coreRadius,
                center = Offset(x, y)
            )
        }
    }
}