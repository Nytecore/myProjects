package com.example.jokeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.example.jokeapp.screens.JokeList
import com.example.jokeapp.ui.theme.JokeAppTheme
import com.example.jokeapp.viewmodel.JokeViewModel

class MainActivity : ComponentActivity() {
    private val viewModel : JokeViewModel by viewModels<JokeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JokeAppTheme {
                LaunchedEffect(Unit) {
                    viewModel.getJokes()
                }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        JokeList(viewModel.jokes)
                    }
                }
            }
        }
    }
}