package com.example.superherobook

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.gson.Gson

@Composable
fun SuperheroList(superheroes: List<Superhero>, navController: NavController) {
    LazyColumn(contentPadding = PaddingValues(5.dp)
        , modifier = Modifier.fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary)

    ) {
        items(superheroes) {superhero ->
            SuperheroRow(superhero = superhero, navController = navController)
        }
    }
}

@Composable
fun SuperheroRow(superhero: Superhero, navController: NavController) {
    Column(
        Modifier
        .fillMaxWidth()
        .background(color = MaterialTheme.colorScheme.primaryContainer)
        .clickable {
            navController.navigate(
                "detail_screen/${Gson().toJson(superhero)}"
            )
        }
    ) {

        Spacer(Modifier.padding(12.dp))
        Text(text = superhero.name,
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(start = 12.dp),
            fontWeight = FontWeight.SemiBold,
        )

        Text(text = superhero.universe,
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(start = 12.dp),
            fontWeight = FontWeight.Normal
        )
    }
}