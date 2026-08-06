package com.leelasri.sparkquiz.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leelasri.sparkquiz.data.model.Question
import com.leelasri.sparkquiz.data.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject
import kotlin.math.ceil

private const val STREAK_MILESTONE = 3
private const val REVEAL_DELAY_MS = 2000L
private const val QUESTION_TIME_LIMIT_MS = 10_000L
private const val TIMER_TICK_MS = 100L

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val repository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var questions: List<Question> = emptyList()
    private var currentIndex = 0
    private var correctCount = 0
    private var skippedCount = 0
    private var currentStreak = 0
    private var longestStreak = 0
    private var revealJob: Job? = null
    private var timerJob: Job? = null

    init {
        loadQuestions()
    }

    fun loadQuestions() {
        revealJob?.cancel()
        timerJob?.cancel()
        viewModelScope.launch {
            _uiState.value = QuizUiState.Loading
            runCatching { repository.getQuestions() }
                .onSuccess { fetched ->
                    if (fetched.isEmpty()) {
                        _uiState.value = QuizUiState.Error("No questions were found. Please try again.")
                        return@onSuccess
                    }
                    questions = fetched
                    resetCounters()
                    emitCurrentQuestion()
                }
                .onFailure { error ->
                    val message = if (error is IOException) {
                        "No internet connection. Check your network and try again."
                    } else {
                        "Couldn't load the quiz. Please try again."
                    }
                    _uiState.value = QuizUiState.Error(message)
                }
        }
    }

    fun selectAnswer(optionIndex: Int) {
        val current = _uiState.value as? QuizUiState.Question ?: return
        if (current.isAnswerRevealed) return

        timerJob?.cancel()

        val isCorrect = optionIndex == current.correctOptionIndex
        if (isCorrect) {
            correctCount++
            currentStreak++
            if (currentStreak > longestStreak) longestStreak = currentStreak
        } else {
            currentStreak = 0
        }
        val hitMilestone = isCorrect && currentStreak > 0 && currentStreak % STREAK_MILESTONE == 0

        _uiState.value = current.copy(
            selectedOptionIndex = optionIndex,
            isAnswerRevealed = true,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            showStreakCelebration = hitMilestone
        )

        revealJob?.cancel()
        revealJob = viewModelScope.launch {
            delay(REVEAL_DELAY_MS)
            advance()
        }
    }

    fun skip() {
        val current = _uiState.value as? QuizUiState.Question ?: return
        if (current.isAnswerRevealed) return

        timerJob?.cancel()
        handleUnanswered()
    }

    fun restart() {
        revealJob?.cancel()
        timerJob?.cancel()
        if (questions.isEmpty()) {
            loadQuestions()
            return
        }
        resetCounters()
        emitCurrentQuestion()
    }

    /** Shared by an explicit Skip tap and a timer expiring — both count as "didn't answer." */
    private fun handleUnanswered() {
        skippedCount++
        currentStreak = 0
        revealJob?.cancel()
        advance()
    }

    private fun advance() {
        currentIndex++
        if (currentIndex >= questions.size) {
            _uiState.value = QuizUiState.Results(
                correctCount = correctCount,
                totalQuestions = questions.size,
                skippedCount = skippedCount,
                longestStreak = longestStreak
            )
        } else {
            emitCurrentQuestion()
        }
    }

    private fun emitCurrentQuestion() {
        val question = questions[currentIndex]
        _uiState.value = QuizUiState.Question(
            questionNumber = currentIndex + 1,
            totalQuestions = questions.size,
            questionText = question.text,
            options = question.options,
            selectedOptionIndex = null,
            correctOptionIndex = question.correctOptionIndex,
            isAnswerRevealed = false,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            showStreakCelebration = false,
            progress = (currentIndex + 1) / questions.size.toFloat(),
            secondsRemaining = (QUESTION_TIME_LIMIT_MS / 1000L).toInt(),
            timerProgress = 1f
        )
        startQuestionTimer()
    }

    /** Ticks down from 10s; auto-skips the question if nobody answers in time. */
    private fun startQuestionTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            while (isActive) {
                val current = _uiState.value as? QuizUiState.Question ?: break
                if (current.isAnswerRevealed) break

                val elapsed = System.currentTimeMillis() - startTime
                val remainingMs = (QUESTION_TIME_LIMIT_MS - elapsed).coerceAtLeast(0L)

                _uiState.value = current.copy(
                    secondsRemaining = ceil(remainingMs / 1000f).toInt(),
                    timerProgress = remainingMs / QUESTION_TIME_LIMIT_MS.toFloat()
                )

                if (remainingMs <= 0L) {
                    handleUnanswered()
                    break
                }
                delay(TIMER_TICK_MS)
            }
        }
    }

    private fun resetCounters() {
        currentIndex = 0
        correctCount = 0
        skippedCount = 0
        currentStreak = 0
        longestStreak = 0
    }

    override fun onCleared() {
        revealJob?.cancel()
        timerJob?.cancel()
        super.onCleared()
    }
}