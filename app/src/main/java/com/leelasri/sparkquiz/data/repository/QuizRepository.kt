package com.leelasri.sparkquiz.data.repository

import com.leelasri.sparkquiz.data.model.Question

interface QuizRepository {
    suspend fun getQuestions(): List<Question>
}