package com.leelasri.sparkquiz.ui.quiz

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.leelasri.sparkquiz.R
import com.leelasri.sparkquiz.ui.components.EncouragementBanner
import com.leelasri.sparkquiz.ui.components.OptionButton
import com.leelasri.sparkquiz.ui.components.OptionState
import com.leelasri.sparkquiz.ui.components.QuestionProgressTicks
import com.leelasri.sparkquiz.ui.components.QuestionTimer
import com.leelasri.sparkquiz.ui.components.SparkMeter
import com.leelasri.sparkquiz.ui.components.StreakCelebrationBanner

@Composable
fun QuizScreen(
    state: QuizUiState.Question,
    onOptionSelected: (Int) -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    LaunchedEffect(state.showStreakCelebration) {
        if (state.showStreakCelebration) {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    // Read the latest state from inside the drag-gesture callback without restarting it
    // every recomposition — avoids skipping a question that already revealed its answer.
    val latestState = rememberUpdatedState(state)

    val isWrongReveal = state.isAnswerRevealed &&
            state.selectedOptionIndex != null &&
            state.selectedOptionIndex != state.correctOptionIndex

    // Pick a new message only the instant a wrong answer is revealed, and hold onto it —
    // never re-read while the banner is fading out, or the text visibly swaps mid-animation.
    val encouragementMessages = stringArrayResource(R.array.encouragement_messages)
    var encouragementMessage by remember { mutableStateOf(encouragementMessages.first()) }
    LaunchedEffect(isWrongReveal) {
        if (isWrongReveal) {
            encouragementMessage = encouragementMessages.random()
        }
    }

    // Same freeze applied to the streak count shown in the celebration banner, for the
    // same reason — it shouldn't change mid-fade just because the next question loaded.
    var celebrationStreakCount by remember { mutableIntStateOf(0) }
    LaunchedEffect(state.showStreakCelebration) {
        if (state.showStreakCelebration) {
            celebrationStreakCount = state.currentStreak
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    if (dragAmount < -60 && !latestState.value.isAnswerRevealed) {
                        onSkip()
                    }
                }
            },
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(
                        R.string.question_progress,
                        state.questionNumber,
                        state.totalQuestions
                    ),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SparkMeter(filled = state.streakPipsFilled)
                    QuestionTimer(
                        secondsRemaining = state.secondsRemaining,
                        progress = state.timerProgress
                    )
                }
            }
            QuestionProgressTicks(current = state.questionNumber, total = state.totalQuestions)
        }

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            StreakCelebrationBanner(
                streakCount = celebrationStreakCount,
                visible = state.showStreakCelebration
            )
            EncouragementBanner(
                message = encouragementMessage,
                visible = isWrongReveal
            )
        }

        AnimatedContent(
            targetState = state.questionNumber,
            transitionSpec = {
                (slideInHorizontally(initialOffsetX = { it }) + fadeIn()) togetherWith
                        (slideOutHorizontally(targetOffsetX = { -it }) + fadeOut())
            },
            label = "questionTransition"
        ) {
            Text(
                text = state.questionText,
                style = MaterialTheme.typography.headlineSmall
            )
        }


        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            state.options.forEachIndexed { index, optionText ->
                val optionState = when {
                    !state.isAnswerRevealed -> OptionState.IDLE
                    index == state.correctOptionIndex -> OptionState.CORRECT
                    index == state.selectedOptionIndex -> OptionState.WRONG_SELECTED
                    else -> OptionState.WRONG_UNSELECTED
                }
                OptionButton(
                    optionLabel = 'A' + index,
                    text = optionText,
                    state = optionState,
                    onClick = { onOptionSelected(index) }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(
            onClick = onSkip,
            enabled = !state.isAnswerRevealed,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(stringResource(R.string.skip))
        }
    }
}