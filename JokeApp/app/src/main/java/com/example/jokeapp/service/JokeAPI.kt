package com.example.jokeapp.service

import com.example.jokeapp.model.JokeModel
import retrofit2.Response
import retrofit2.http.GET

interface JokeAPI {

    //ENDPOINT --> atilsamancioglu/ProgrammingJokesJSON/main/jokes.json

    @GET("atilsamancioglu/ProgrammingJokesJSON/main/jokes.json")
    suspend fun getJoke(): Response<List<JokeModel>>
}