package com.example.jokeapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jokeapp.model.JokeModel

@Composable
fun JokeList(jokeList: List<JokeModel>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primaryContainer)
    ) {
        items(jokeList) {joke ->
            JokeRow(joke)
        }
    }
}

@Composable
fun JokeRow(joke: JokeModel) {
    Column(
        modifier = Modifier.padding(10.dp)
    ) {
        if (joke.type == "single") {
            Text(
                text = joke.joke,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth()
            )
        } else if (joke.type == "twopart") {
            Text(
                text = "Setup: ${joke.setup}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Delivery: ${joke.delivery}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    HorizontalDivider(
        thickness = 6.dp,
        color = Color.Black
    )
}