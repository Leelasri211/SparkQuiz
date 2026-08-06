package com.leelasri.sparkquiz.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun QuestionTimer(secondsRemaining: Int, progress: Float, modifier: Modifier = Modifier) {
    val isUrgent = secondsRemaining <= 3
    val ringColor by animateColorAsState(
        targetValue = if (isUrgent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        label = "timerRingColor"
    )
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Box(modifier = modifier.size(30.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 3.dp.toPx()
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        Text(
            text = secondsRemaining.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = ringColor
        )
    }
}