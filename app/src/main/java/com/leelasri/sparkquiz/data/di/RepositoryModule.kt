package com.leelasri.sparkquiz.data.di

import com.leelasri.sparkquiz.data.repository.QuizRepository
import com.leelasri.sparkquiz.data.repository.QuizRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindQuizRepository(impl: QuizRepositoryImpl): QuizRepository
}