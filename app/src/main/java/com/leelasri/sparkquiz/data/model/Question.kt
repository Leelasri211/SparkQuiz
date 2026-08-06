package com.leelasri.sparkquiz.data.model

import com.google.gson.annotations.SerializedName

data class QuestionDto(
    @SerializedName("id") val id: Int,
    @SerializedName("question") val question: String,
    @SerializedName("options") val options: List<String>,
    @SerializedName("correctOptionIndex") val correctOptionIndex: Int
)

data class Question(
    val id: Int,
    val text: String,
    val options: List<String>,
    val correctOptionIndex: Int
)

fun QuestionDto.toDomain(): Question = Question(
    id = id,
    text = question,
    options = options,
    correctOptionIndex = correctOptionIndex.coerceIn(0, (options.size - 1).coerceAtLeast(0))
)