package com.leelasri.sparkquiz.data.remote

import com.leelasri.sparkquiz.data.model.QuestionDto
import retrofit2.http.GET

interface QuizApiService {

    @GET("dr-samrat/53846277a8fcb034e482906ccc0d12b2/raw")
    suspend fun getQuestions(): List<QuestionDto>
}