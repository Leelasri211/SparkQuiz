package com.leelasri.sparkquiz.data.repository

import com.leelasri.sparkquiz.data.model.Question
import com.leelasri.sparkquiz.data.model.toDomain
import com.leelasri.sparkquiz.data.remote.QuizApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class QuizRepositoryImpl @Inject constructor(
    private val apiService: QuizApiService
) : QuizRepository {

    override suspend fun getQuestions(): List<Question> = withContext(Dispatchers.IO) {
        apiService.getQuestions()
            .map { it.toDomain() }
            .filter { it.options.isNotEmpty() && it.text.isNotBlank() }
    }
}