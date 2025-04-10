package com.example.travelbook.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.travelbook.R
import com.example.travelbook.adapter.PlaceAdapter
import com.example.travelbook.databinding.ActivityMainBinding
import com.example.travelbook.model.Place
import com.example.travelbook.roomdb.PlaceDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isVisible: Boolean? = null
    private val compositeDisposable = CompositeDisposable()

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

        val db = Room.databaseBuilder(applicationContext , PlaceDatabase::class.java , "Places").build()
        val placeDao = db.placeDao()

        compositeDisposable.add(
            placeDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)
        )

        binding.openFabButton.setOnClickListener {

            isVisible = false
            if (binding.locationFabButton.isVisible == false) {
                binding.locationFabButton.visibility = View.VISIBLE
                binding.openFabButton.setImageResource(R.drawable.fab_close_icon)
                isVisible = true
            } else {
                binding.locationFabButton.visibility = View.GONE
                binding.openFabButton.setImageResource(R.drawable.fab_add_icon)
                isVisible = false
            }
        }

        binding.locationFabButton.setOnClickListener {

            val intent = Intent(this , MapsActivity::class.java)
            intent.putExtra("info" , "new")
            startActivity(intent)

        }
    }

    private fun handleResponse(placeList: List<Place>) {
        //Show RecyclerView placeList here.
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = PlaceAdapter(placeList)
        binding.recyclerView.adapter = adapter
    }
}