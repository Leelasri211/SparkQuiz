package com.leelasri.sparkquiz.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.leelasri.sparkquiz.ui.quiz.QuizScreen
import com.leelasri.sparkquiz.ui.quiz.QuizUiState
import com.leelasri.sparkquiz.ui.quiz.QuizViewModel
import com.leelasri.sparkquiz.ui.results.ResultsScreen
import com.leelasri.sparkquiz.ui.splash.SplashScreen

/**
 * Single entry point into the quiz flow. One [QuizViewModel] drives all three screens —
 * Splash/Error -> Question -> Results — switched purely off [QuizUiState], so there's no
 * navigation library, no argument-passing, and no risk of screens seeing stale state.
 */
@Composable
fun SparkQuizApp(viewModel: QuizViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(top = 12.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        AnimatedContent(
            targetState = uiState,
            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
            contentKey = { it::class },
            label = "screenTransition"
        ) { state ->
            when (state) {
                is QuizUiState.Loading -> SplashScreen(
                    isError = false,
                    errorMessage = null,
                    onRetry = {}
                )

                is QuizUiState.Error -> SplashScreen(
                    isError = true,
                    errorMessage = state.message,
                    onRetry = viewModel::loadQuestions
                )

                is QuizUiState.Question -> QuizScreen(
                    state = state,
                    onOptionSelected = viewModel::selectAnswer,
                    onSkip = viewModel::skip
                )

                is QuizUiState.Results -> ResultsScreen(
                    state = state,
                    onRestart = viewModel::restart
                )
            }
        }
    }
}