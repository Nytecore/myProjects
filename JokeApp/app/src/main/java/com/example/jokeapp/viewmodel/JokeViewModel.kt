package com.example.jokeapp.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.example.jokeapp.model.JokeModel
import com.example.jokeapp.service.JokeAPI
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class JokeViewModel(application: Application) : AndroidViewModel(application) {

    //BASE_URL --> https://raw.githubusercontent.com/

    private val BASE_URL = "https://raw.githubusercontent.com/"

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(JokeAPI::class.java)

    var jokes by mutableStateOf<List<JokeModel>>(emptyList())
        private set

    fun getJokes() {
        viewModelScope.launch {
            try {
                val response = retrofit.getJoke()
                if(response.isSuccessful) {
                    jokes = response.body() ?: emptyList()
                } else {
                    Toast.makeText(application.baseContext, "Response is failure",Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}