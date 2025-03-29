package com.example.artbookproject

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.artbookproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var fabVisibility = false
    private lateinit var artList: ArrayList<Art>
    private lateinit var artAdapter: ArtAdapter

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

        artList = ArrayList<Art>()
        artAdapter = ArtAdapter(artList)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = artAdapter

        pullData()


        fabVisibility = false

        binding.fabOpenButton.setOnClickListener {

            if (!fabVisibility) {
                binding.fabAddButton.show()
                binding.fabOpenButton.setImageResource(R.drawable.close_image)
                fabVisibility = true

            } else {
                binding.fabAddButton.visibility = View.GONE
                binding.fabOpenButton.setImageResource(R.drawable.open_image)
                fabVisibility = false

            }

        }

        binding.fabAddButton.setOnClickListener {
            val intent = Intent(this@MainActivity, ArtActivity::class.java)
            intent.putExtra("info" , "new")
            startActivity(intent)
        }

    }

    private fun pullData() {

        try {

            val database = this.openOrCreateDatabase("Arts" , MODE_PRIVATE , null)
            val cursor = database.rawQuery("SELECT * FROM arts " , null)
            val artNameIx = cursor.getColumnIndex("artname")
            val idIx = cursor.getColumnIndex("id")

            while (cursor.moveToNext()) {
                val name = cursor.getString(artNameIx)
                val id = cursor.getInt(idIx)

                val art = Art(name , id)
                artList.add(art)
            }

            artAdapter.notifyDataSetChanged()

            cursor.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
        // Data pull operations
}