package com.leelasri.sparkquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.leelasri.sparkquiz.ui.SparkQuizApp
import com.leelasri.sparkquiz.ui.theme.SparkQuizTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            SparkQuizTheme {
                SparkQuizApp()
            }
        }
    }
}