package com.example.cloneinstagram

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cloneinstagram.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
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

        auth = Firebase.auth
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val intent = Intent(this@MainActivity,FeedActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.singUpButton.setOnClickListener {

            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {

                auth.createUserWithEmailAndPassword(email , password).addOnSuccessListener {
                     //Success
                    val intent = Intent(this@MainActivity,FeedActivity::class.java)
                    startActivity(intent)
                    finish()

                }.addOnFailureListener {
                    //Failure
                    Toast.makeText(this@MainActivity , it.localizedMessage , Toast.LENGTH_LONG).show()
                }

            } else {
                if (email.equals("")) {
                    Toast.makeText(this , "E-mail cannot be empty" , Toast.LENGTH_LONG).show()

                } else if (password.equals("")) {
                    Toast.makeText(this , "Password cannot be empty" , Toast.LENGTH_LONG).show()

                } else {
                    Toast.makeText(this , "E-mail and password cannot be empty" , Toast.LENGTH_LONG).show()

                }
            }
        }

        binding.signInButton.setOnClickListener {

            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {

                auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
                    val intent = Intent(this@MainActivity,FeedActivity::class.java)
                    startActivity(intent)
                    finish()

                }.addOnFailureListener {
                    Toast.makeText(this@MainActivity,it.localizedMessage,Toast.LENGTH_LONG).show()

                }

            } else {

                if (email.equals("")) {
                    Toast.makeText(this@MainActivity, "E-mail cannot be empty", Toast.LENGTH_LONG)
                        .show()

                } else if (password.equals("")) {
                    Toast.makeText(this@MainActivity,"Password cannot be empty",Toast.LENGTH_LONG).show()

                } else {
                    Toast.makeText(this , "E-mail and password cannot be empty" , Toast.LENGTH_LONG).show()

                }
            }


        }

    }
}