package com.example.retrofitkotlin.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrofitkotlin.R
import com.example.retrofitkotlin.adapter.CryptoAdapter
import com.example.retrofitkotlin.databinding.ActivityMainBinding
import com.example.retrofitkotlin.model.CryptoData
import com.example.retrofitkotlin.model.CryptoModel
import com.example.retrofitkotlin.service.CryptoAPI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private val BASE_URL = "https://pro-api.coinmarketcap.com/v1/"
    private var cryptoModels: ArrayList<CryptoData> ? = null
    private lateinit var binding: ActivityMainBinding
    private var compositeDisposable : CompositeDisposable? = null

    // Coin market cap API Server URL: https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest
    // BASE URL: https://pro-api.coinmarketcap.com/v1/
    // ENDPOINT: v1/cryptocurrency/listings/latest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        compositeDisposable = CompositeDisposable()
        loadData()


    }

    private fun loadData() {

        /* ----->> Using Call
        val service = retrofit.create(CryptoAPI::class.java)
        val call = service.getData(1,100,"USD")

        call.enqueue(object : Callback<CryptoModel> {
            override fun onResponse(p0: Call<CryptoModel>, p1: Response<CryptoModel>) {

                if (p1.isSuccessful) {
                    p1.body()?.let {
                        cryptoModels = ArrayList(it.data)
                        val cryptoAdapter = CryptoAdapter(it.data)
                        binding.recyclerView.adapter = cryptoAdapter
                        binding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                    }
                }
            }

            override fun onFailure(p0: Call<CryptoModel>, p1: Throwable) {
                p1.printStackTrace()
            }
        })*/

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build().create(CryptoAPI::class.java)

        compositeDisposable?.add(
            retrofit.getData(1 , 100 , "USD")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    handleResponse(it.data)
                },{
                    it.printStackTrace()
                })
        )
    }

    private fun handleResponse(cryptoList: List<CryptoData>) {
        cryptoModels = ArrayList(cryptoList)
        cryptoModels?.let {
            val cryptoAdapter = CryptoAdapter(it)
            binding.recyclerView.adapter = cryptoAdapter
            binding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable?.clear()
    }
}
