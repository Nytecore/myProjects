package com.example.cloneinstagram

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cloneinstagram.databinding.ActivityFeedBinding
import com.example.cloneinstagram.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FeedActivity : AppCompatActivity() {
    private lateinit var binding : ActivityFeedBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var postArrayList : ArrayList<Post>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFeedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

            // Set toolbar action bar
        setSupportActionBar(binding.toolbar)

            // Initialize authentication
        auth = Firebase.auth

            // Initialize firestore database
        db = Firebase.firestore

            // Initialize postArrayList for fields
        postArrayList = ArrayList<Post>()


        getData()



    }

        //get data from database
    private fun getData() {
            //create method for get data
        db.collection("Posts").addSnapshotListener { value, error ->
                //error nullable control
            if (error != null) {
                Toast.makeText(this@FeedActivity , error.localizedMessage , Toast.LENGTH_LONG).show()
            } else {
                    //value nullable control
                if (value != null) {
                        //value is not empty control
                    if (!value.isEmpty) {
                        val documents = value.documents
                            //get document from documents here
                        for (document in documents) {
                            //get datas here with reference from firestore and use casting
                            val comment = document.get("comment") as String
                            val userEmail = document.get("userEmail") as String
                            val downloadUrl = document.get("downloadUrl") as String


                                //create "post" class for get all fields in ArrayList
                            val post = Post(userEmail , comment, downloadUrl)
                            postArrayList.add(post)

                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.insta_menu , menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_post) {
            val intent = Intent(this@FeedActivity,UploadActivity::class.java)
            startActivity(intent)
        } else if (item.itemId == R.id.signout) {
            auth.signOut()
            val intent = Intent(this@FeedActivity , MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}