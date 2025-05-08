package com.example.retrofitkotlin.model


data class CryptoModel(
    val data: List<CryptoData>
)

data class CryptoData(
    val name: String,
    val symbol: String,
    val quote: QuoteMap
)

data class QuoteMap(
    val USD: Quote
)

data class Quote(
    val price: Float
)



