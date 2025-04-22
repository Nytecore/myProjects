package com.example.cloneinstagram.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cloneinstagram.R
import com.example.cloneinstagram.databinding.ActivityUploadBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture : Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUploadBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Register ActivityResultLauncher
        registerLauncher()

        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage

        binding.imageView.setOnClickListener{

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // If SDK Version > = Android TIRAMISU (33+)    ---> READ_MEDIA_IMAGES
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_MEDIA_IMAGES)) {
                        Snackbar.make(view, "Permission needed for gallery!" , Snackbar.LENGTH_INDEFINITE).setAction("Give Permission") {
                            //Request permission
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        }
                    } else {
                        // Request permission
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)

                    }
                } else {
                    val intentToGallery = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                    //Start activity for result
                }

            } else {
                // ----> READ_EXTERNAL_STORAGE

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Snackbar.make(view, "Permission needed for gallery!" , Snackbar.LENGTH_INDEFINITE).setAction("Give Permission") {
                            //Request permission
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    } else {
                        // Request permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                    }
                } else {
                    val intentToGallery = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                    //Start activity for result
                }
            }
        }

        binding.uploadButton.setOnClickListener {

            //Universal Unique ID
            val uuid = UUID.randomUUID()
            val imageName = "$uuid.jpg"

            val reference = storage.reference
            val imageReference = reference.child("images/$imageName")

            if (selectedPicture != null) {
                imageReference.putFile(selectedPicture!!).addOnSuccessListener {

                    // download url ---> Firestore
                    val uploadPictureReference = storage.reference.child("images/$imageName")
                    uploadPictureReference.downloadUrl.addOnSuccessListener {

                        val downloadUrl = it.toString()

                        if (auth.currentUser != null) {
                            val postMap = hashMapOf<String,Any>()
                            postMap.put("userEmail" , auth.currentUser!!.email!!)
                            postMap.put("downloadUrl" , downloadUrl)
                            postMap.put("comment" , binding.commentText.text.toString())
                            postMap.put("date" , Timestamp.now())

                            firestore.collection("Posts")
                                .add(postMap)
                                .addOnSuccessListener {
                                    finish()

                                }.addOnFailureListener {
                                    Toast.makeText(this@UploadActivity , it.localizedMessage , Toast.LENGTH_LONG).show()

                                }
                        }

                    }.addOnFailureListener {
                        //Chech debugging
                        println(it)

                    }

                }.addOnFailureListener {
                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()

                }
            }
        }
    }

    private fun registerLauncher() {

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->

            if (result.resultCode == RESULT_OK) {
                //If user selected an image
                val intentFromResult = result.data
                if (intentFromResult != null) {
                   selectedPicture = intentFromResult.data
                    selectedPicture?.let {
                        binding.imageView.setImageURI(it)
                    }
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {result ->

            if (result) {
                //Permission Granted
                val intentToGallery = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)

            } else {
                //Permission Denied
                Toast.makeText(this@UploadActivity, "Permission Needed!",Toast.LENGTH_LONG).show()

            }
        }
    }
}