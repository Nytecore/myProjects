package com.example.retrofitkotlin.service

import com.example.retrofitkotlin.model.CryptoModel
import io.reactivex.rxjava3.core.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface CryptoAPI {

    // Get data ---> GET
    // Write data ---> POST
    // Update data ---> UPDATE
    // Delete data ---> DELETE

    // Coin market cap API Server URL: https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest
    // Coin market cap API Key:

    @Headers("X-CMC_PRO_API_KEY: YOUR_API_KEY_HERE")
    @GET("cryptocurrency/listings/latest")
    fun getData(
        @Query("start") start: Int,
        @Query("limit") limit: Int = 100,
        @Query("convert") convert: String = "USD"
    ): Observable<CryptoModel>


}





