package com.example.shoppinglist.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil3.compose.rememberAsyncImagePainter
import com.example.shoppinglist.R
import com.example.shoppinglist.model.Item
import java.io.ByteArrayOutputStream
import androidx.core.graphics.scale

@Composable
fun AddItemScreen(saveFunction: (Item) -> Unit) {

    val itemName = remember { mutableStateOf("") }
    val storeName = remember { mutableStateOf("") }
    val price = remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    Box (modifier = Modifier.fillMaxSize()
        .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ){

        Column (horizontalAlignment = Alignment.CenterHorizontally
        ){

            ImagePicker{uri ->
                selectedImageUri = uri
            }

            TextField(value = itemName.value,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ), placeholder = {
                    Text("Enter Item Name")
                }, onValueChange = {
                    itemName.value = it
                }
            )

            TextField(value = storeName.value,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ), placeholder = {
                    Text("Enter Store Name")
                }, onValueChange = {
                    storeName.value = it
                }
            )

            TextField(value = price.value,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ), placeholder = {
                    Text("Enter Price")
                }, onValueChange = {
                    price.value = it
                }
            )

            Spacer(Modifier.padding(10.dp))

            Button(onClick = {
                //Save item transactions

                val imageByteArray = selectedImageUri?.let {
                    resizeImage(context = context, uri = it, maxWidth = 600, maxHeight = 400)
                } ?: ByteArray(0)

                val itemToInsert = Item(
                    itemName = itemName.value,
                    storeName = storeName.value,
                    price = price.value,
                    image = imageByteArray
                )

                saveFunction(itemToInsert)

            }) {
                Text("SAVE ITEM")
            }
        }
    }
}

@Composable
fun ImagePicker(onImageSelected: (Uri?) -> Unit) {
    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val context = LocalContext.current
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val galleryLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {uri ->
        selectedImageUri = uri
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {granted ->
            if (granted) {
                galleryLauncher.launch("image/*")
            } else {
                Toast.makeText(context, "Request denied", Toast.LENGTH_SHORT).show()
            }
        }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        selectedImageUri?.let {
            Image(painter = rememberAsyncImagePainter(it),
                "User's selected image",
                modifier = Modifier.size(width = 500.dp, height = 350.dp)
                    .padding(16.dp)
            )

            onImageSelected(it)

        } ?: Image(
            painter = painterResource(R.drawable.select_image),
            contentDescription = "Null image",
            modifier = Modifier.size(width = 500.dp, height = 350.dp)
                .padding(16.dp)
                .clickable {
                    if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                        galleryLauncher.launch("image/*")
                    } else {
                        permissionLauncher.launch(permission)
                    }
                }
        )
    }
}

fun resizeImage(context: Context, uri: Uri, maxWidth: Int, maxHeight: Int) : ByteArray? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)

        val ratio = originalBitmap.width.toFloat() / originalBitmap.height.toFloat()

        var width = maxWidth
        var height = (width / ratio).toInt()

        if (height > maxHeight) {
            height = maxHeight
            width  = (height * ratio).toInt()
        }

        val resizedBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // API 30+ --> .scale()
            originalBitmap.scale(width, height, true)
        } else {
            Bitmap.createScaledBitmap(originalBitmap, width, height, true)
        }

        val byteArrayOutputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        byteArrayOutputStream.toByteArray()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}