package com.leelasri.sparkquiz.ui.quiz

sealed interface QuizUiState {

    data object Loading : QuizUiState

    data class Error(val message: String) : QuizUiState

    data class Question(
        val questionNumber: Int,
        val totalQuestions: Int,
        val questionText: String,
        val options: List<String>,
        val selectedOptionIndex: Int?,
        val correctOptionIndex: Int,
        val isAnswerRevealed: Boolean,
        val currentStreak: Int,
        val longestStreak: Int,
        val showStreakCelebration: Boolean,
        val progress: Float,
        val secondsRemaining: Int,
        val timerProgress: Float
    ) : QuizUiState {
        /** How many of the 3 streak "pips" should look filled right now (cycles every 3). */
        val streakPipsFilled: Int
            get() = when {
                currentStreak <= 0 -> 0
                currentStreak % 3 == 0 -> 3
                else -> currentStreak % 3
            }
    }

    data class Results(
        val correctCount: Int,
        val totalQuestions: Int,
        val skippedCount: Int,
        val longestStreak: Int
    ) : QuizUiState
}