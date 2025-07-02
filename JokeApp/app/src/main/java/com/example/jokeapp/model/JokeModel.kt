package com.example.jokeapp.model

data class JokeModel(
    val error: Boolean = false,
    val category: String = "",
    val type: String = "",
    val setup: String = "",
    val joke: String = "",
    val delivery: String = "",
    val flags: Flags = Flags(),
    val id: Int = 0,
    val safe: Boolean = true,
    val lang: String = ""
)

data class Flags(
    val nsfw: Boolean = false,
    val religious: Boolean = false,
    val political: Boolean = false,
    val racist: Boolean = false,
    val sexist: Boolean = false,
    val explicit: Boolean = false
)