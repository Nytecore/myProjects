package com.example.superherobook

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun DetailScreen(superhero: Superhero) {
    Box(
        Modifier.fillMaxSize()
        .background(color = MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(text = superhero.name,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(5.dp),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Image(bitmap = ImageBitmap.imageResource(superhero.image),
                contentDescription = "Superhero Name: ${superhero.name}",
                modifier = Modifier
                    .padding(top = 16.dp)
                    .size(width = 350.dp, height = 350.dp)
            )

            Text(text = superhero.universe,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(5.dp),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Text(text = superhero.skill,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(5.dp),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }
    }
}