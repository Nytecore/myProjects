package com.example.artbookproject

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.artbookproject.databinding.ActivityArtBinding
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream

class ArtActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArtBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedBitmap: Bitmap? = null
    private lateinit var database: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityArtBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        database = this.openOrCreateDatabase("Arts" , MODE_PRIVATE , null)
        registerLauncher()

        val intent = intent
        val info = intent.getStringExtra("info")

        if (info.equals("new")) {
            // User wants to create new art
            binding.artNameText.setText("")
            binding.artistNameText.setText("")
            binding.yearText.setText("")
            binding.saveButton.visibility = View.VISIBLE
            binding.imageView.setImageResource(R.drawable.select_image)
        } else {
            // User wants to see oldest art
            binding.saveButton.visibility = View.INVISIBLE
            val selectedId = intent.getIntExtra("id" , 1)
            val cursor = database.rawQuery("SELECT * FROM arts WHERE id = ?" , arrayOf(selectedId.toString()))
            val artNameIx = cursor.getColumnIndex("artname")
            val artistNameIx = cursor.getColumnIndex("artistname")
            val yearIx = cursor.getColumnIndex("year")
            val imageIx = cursor.getColumnIndex("image")
            binding.createTextView.text = "ART"

            while (cursor.moveToNext()) {
                binding.artNameText.setText(cursor.getString(artNameIx))
                binding.artistNameText.setText(cursor.getString(artistNameIx))
                binding.yearText.setText(cursor.getString(yearIx))

                val byteArray = cursor.getBlob(imageIx)
                val bitmap = BitmapFactory.decodeByteArray(byteArray , 0 , byteArray.size)
                binding.imageView.setImageBitmap(bitmap)
            }
            cursor.close()
        }

        binding.imageView.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    //SDK Version Control for TIRAMISU
                        // Android 33+  -->  READ_MEDIA_IMAGES

                if (ContextCompat.checkSelfPermission(this , Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)) {
                        //rationale

                        Snackbar.make(it , "Permission needed for gallery" , Snackbar.LENGTH_INDEFINITE).setAction("Give Permission" , View.OnClickListener {
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                            //request permission

                        }).show()

                    } else {
                        //request permission
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }
                } else {
                    val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)

                }
            } else {
                // Android 32-  ----> READ_EXTERNAL_STORAGE

                if (ContextCompat.checkSelfPermission(this , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        //rationale

                        Snackbar.make(it , "Permission needed for gallery." , Snackbar.LENGTH_INDEFINITE).setAction("Give Permission" , View.OnClickListener {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            //request permission

                        }).show()

                    } else {
                        //request permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                    }
                } else {
                    val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)

                }
            }
        }

        binding.saveButton.setOnClickListener {

            val artName = binding.artNameText.text.toString()
            val artistName = binding.artistNameText.text.toString()
            val year = binding.yearText.text.toString()

            if (selectedBitmap != null) {

                val smallBitmap = makeSmallerBitmap(selectedBitmap!! , 300)
                val outputStream = ByteArrayOutputStream()
                smallBitmap.compress(Bitmap.CompressFormat.PNG, 50 , outputStream)
                val byteArray = outputStream.toByteArray()

                try {
                    //Creating database and save operations
                    //val database = this.openOrCreateDatabase("Arts" , MODE_PRIVATE , null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS arts (id INTEGER PRIMARY KEY , artname VARCHAR , artistname VARCHAR , year VARCHAR , image BLOB)")

                    val sqlString = "INSERT INTO arts (artname , artistname , year , image) VALUES (? , ? , ? , ?)"
                    val statement = database.compileStatement(sqlString)

                        //binding
                    statement.bindString(1 , artName)
                    statement.bindString(2 , artistName)
                    statement.bindString(3 , year)
                    statement.bindBlob(4 , byteArray)
                    statement.execute()

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                val intent = Intent(this@ArtActivity , MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        }
    }

    @SuppressLint("UseKtx")
    private fun makeSmallerBitmap(image: Bitmap, maximumSize: Int) : Bitmap {
            // Convert Bitmap to Smaller Bitmap

        var width = image.width
        var height = image.height
        val bitmapRatio : Double = width.toDouble() / height.toDouble()

        if (bitmapRatio > 1) {
            // image = landscape
            width = maximumSize
            val scaledHeight = width / bitmapRatio
            height = scaledHeight.toInt()

        } else {
            // image = portrait
            height = maximumSize
            val scaledWidth = height * bitmapRatio
            width = scaledWidth.toInt()
        }

        val versionControl =
            if (Build.VERSION.SDK_INT >= 30) {
                image.scale(width,height)
            } else {
                Bitmap.createScaledBitmap(image, width,height , true)
            }

        return versionControl
    }


        // Request Permission and Intent to gallery operations --->
    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
            //Intent
            if (result.resultCode == RESULT_OK) {
                val intentFromResult = result.data
                    // intentFromResult --> Intent?

                if (intentFromResult != null) {
                    val imageData = intentFromResult.data
                        // imageData --> Uri?

                    if (imageData != null) {
                        try {
                            if (Build.VERSION.SDK_INT >= 28) {
                                val source = ImageDecoder.createSource(this@ArtActivity.contentResolver,imageData)
                                selectedBitmap = ImageDecoder.decodeBitmap(source)
                                binding.imageView.setImageBitmap(selectedBitmap)
                            } else {
                                selectedBitmap = MediaStore.Images.Media.getBitmap(contentResolver,imageData)
                                binding.imageView.setImageBitmap(selectedBitmap)
                            }

                        } catch (e: Exception){
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {resut ->
            //Permission
            if (resut) {
                //permission granted.
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)

            } else {
                //permission denied.
                Toast.makeText(this@ArtActivity, "Permission needed!", Toast.LENGTH_LONG).show()
            }
        }

    }

}