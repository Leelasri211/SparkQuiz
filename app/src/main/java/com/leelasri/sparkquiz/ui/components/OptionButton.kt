package com.leelasri.sparkquiz.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.leelasri.sparkquiz.R

enum class OptionState { IDLE, CORRECT, WRONG_SELECTED, WRONG_UNSELECTED }

@Composable
fun OptionButton(
    optionLabel: Char,
    text: String,
    state: OptionState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme

    val containerColor by animateColorAsState(
        targetValue = when (state) {
            OptionState.IDLE -> scheme.surface
            OptionState.CORRECT -> scheme.tertiary.copy(alpha = 0.16f)
            OptionState.WRONG_SELECTED -> scheme.error.copy(alpha = 0.16f)
            OptionState.WRONG_UNSELECTED -> scheme.surface.copy(alpha = 0.5f)
        },
        label = "optionContainer"
    )
    val borderColor by animateColorAsState(
        targetValue = when (state) {
            OptionState.IDLE -> scheme.outline
            OptionState.CORRECT -> scheme.tertiary
            OptionState.WRONG_SELECTED -> scheme.error
            OptionState.WRONG_UNSELECTED -> scheme.outline.copy(alpha = 0.4f)
        },
        label = "optionBorder"
    )
    val badgeColor by animateColorAsState(
        targetValue = when (state) {
            OptionState.IDLE, OptionState.WRONG_UNSELECTED -> scheme.surfaceVariant
            OptionState.CORRECT -> scheme.tertiary
            OptionState.WRONG_SELECTED -> scheme.error
        },
        label = "optionBadge"
    )
    val badgeContentColor = when (state) {
        OptionState.IDLE, OptionState.WRONG_UNSELECTED -> scheme.onSurfaceVariant
        OptionState.CORRECT -> scheme.onTertiary
        OptionState.WRONG_SELECTED -> scheme.onError
    }

    // One-shot horizontal shake, only when this option is revealed as the wrong pick.
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(state) {
        if (state == OptionState.WRONG_SELECTED) {
            shakeOffset.snapTo(0f)
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 400
                    0f at 0
                    -10f at 50
                    10f at 110
                    -8f at 170
                    8f at 230
                    -3f at 290
                    3f at 340
                    0f at 400
                }
            )
        }
    }

    Surface(
        onClick = onClick,
        enabled = state == OptionState.IDLE,
        modifier = modifier
            .fillMaxWidth()
            .offset(x = shakeOffset.value.dp),
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        contentColor = scheme.onSurface,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier.size(42.dp),
                contentAlignment = Alignment.Center
            ) {
                if (state == OptionState.CORRECT) {
                    CorrectBurst(color = scheme.tertiary, modifier = Modifier.fillMaxSize())
                }
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(badgeColor),
                    contentAlignment = Alignment.Center
                ) {
                    when (state) {
                        OptionState.CORRECT -> {
                            val iconScale = remember { Animatable(0.3f) }
                            LaunchedEffect(Unit) {
                                iconScale.animateTo(
                                    targetValue = 1f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    )
                                )
                            }
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = stringResource(R.string.cd_correct_answer),
                                tint = badgeContentColor,
                                modifier = Modifier
                                    .size(16.dp)
                                    .scale(iconScale.value)
                            )
                        }

                        OptionState.WRONG_SELECTED -> {
                            val iconScale = remember { Animatable(0.3f) }
                            LaunchedEffect(Unit) {
                                iconScale.animateTo(
                                    targetValue = 1f,
                                    animationSpec = tween(180, easing = FastOutSlowInEasing)
                                )
                            }
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = stringResource(R.string.cd_wrong_answer),
                                tint = badgeContentColor,
                                modifier = Modifier
                                    .size(16.dp)
                                    .scale(iconScale.value)
                            )
                        }

                        else -> Text(
                            text = optionLabel.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = badgeContentColor
                        )
                    }
                }
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/** One-shot expanding ring that fades out — the "success" flourish behind a correct badge. */
@Composable
private fun CorrectBurst(color: Color, modifier: Modifier = Modifier) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(1f, animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing))
    }
    Canvas(modifier = modifier) {
        val baseRadius = size.minDimension / 2.6f
        drawCircle(
            color = color.copy(alpha = (1f - progress.value).coerceIn(0f, 1f) * 0.6f),
            radius = baseRadius * (1f + progress.value * 0.6f),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}