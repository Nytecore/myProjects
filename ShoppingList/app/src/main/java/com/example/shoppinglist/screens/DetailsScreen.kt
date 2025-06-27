package com.example.shoppinglist.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoppinglist.R
import com.example.shoppinglist.model.Item

@Composable
fun DetailScreen(item: Item?, deleteFunction: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()
        .background(color = MaterialTheme.colorScheme.primaryContainer)
        , contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(text = item?.itemName ?: "",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.Black,
                modifier = Modifier.padding(2.dp)
            )

            val imageBitmap = item?.image?.let {byteArray ->
                BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)?.asImageBitmap()
            }

            Image(
                bitmap = imageBitmap ?: ImageBitmap.imageResource(id = R.drawable.select_image),
                contentDescription = "item image",
                modifier = Modifier.padding(16.dp)
                    .size(width = 450.dp, height = 350.dp)
            )

            Text(text = item?.storeName ?: "",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = Color.Black,
                modifier = Modifier.padding(2.dp)
            )

            Text(text = item?.price ?: "",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = Color.Black,
                modifier = Modifier.padding(2.dp)
            )

            Button(onClick = {
                //Delete transactions
                deleteFunction()

            }) {
                Text("DELETE")
            }
        }
    }
}