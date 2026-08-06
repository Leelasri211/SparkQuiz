package com.leelasri.sparkquiz.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.leelasri.sparkquiz.R

@Composable
fun SparkMeter(filled: Int, modifier: Modifier = Modifier) {
    val barHeights = listOf(8.dp, 14.dp, 20.dp)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        barHeights.forEachIndexed { index, maxHeight ->
            val isFilled = index < filled
            val animatedHeight by animateDpAsState(
                targetValue = if (isFilled) maxHeight else maxHeight * 0.5f,
                label = "sparkBarHeight"
            )
            val color by animateColorAsState(
                targetValue = if (isFilled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                label = "sparkBarColor"
            )
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(animatedHeight)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
        }
    }
}

@Composable
fun StreakCelebrationBanner(streakCount: Int, visible: Boolean, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(initialScale = 0.6f) + fadeIn(),
        exit = scaleOut(targetScale = 0.6f) + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(50),
            color = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Text(
                text = stringResource(R.string.streak_celebration, streakCount),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}