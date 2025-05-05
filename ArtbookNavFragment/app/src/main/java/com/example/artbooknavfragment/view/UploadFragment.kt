package com.example.artbooknavfragment.view


import com.example.artbooknavfragment.R
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
import androidx.room.Room
import com.example.artbooknavfragment.databinding.FragmentUploadBinding
import com.example.artbooknavfragment.model.Art
import com.example.artbooknavfragment.roomdb.ArtDao
import com.example.artbooknavfragment.roomdb.ArtDatabase
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import androidx.navigation.findNavController
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers


class UploadFragment : Fragment() {
    private var _binding : FragmentUploadBinding? = null
    private val binding get() = _binding!!
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var selectedBitmap : Bitmap? = null
    private lateinit var database: ArtDatabase
    private lateinit var artDao: ArtDao
    private val compositeDisposable = CompositeDisposable()
    private var isArtNull : Art? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerLauncher()
            // Register permissions ---> Start activity for result

        database = Room.databaseBuilder(
            requireContext(),
            ArtDatabase::class.java,
            "Arts"
        ).build()
            // Register Database

        artDao = database.artDao()
            // Register DAO

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUploadBinding.inflate(inflater , container , false)
        val view = binding.root
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        arguments?.let {

            val getData = UploadFragmentArgs.fromBundle(it).info

            if (getData.equals("new")) {
                // User wants to create new art

                binding.deleteButton.visibility = View.GONE
                binding.createButton.visibility = View.VISIBLE

                binding.artNameText.setText("")
                binding.artistNameText.setText("")
                binding.yearText.setText("")
                binding.imageView.setImageResource(R.drawable.select_image)

            } else {
                // User wants to see oldest art

                binding.deleteButton.visibility = View.VISIBLE
                binding.createButton.visibility = View.GONE

                    // Companent usages disabled
                binding.imageView.isEnabled = false
                binding.artNameText.isEnabled = false
                binding.artistNameText.isEnabled = false
                binding.yearText.isEnabled = false

                    // Text alignment --> Center
                binding.artNameText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                binding.artistNameText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                binding.yearText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER

                val selectedId = UploadFragmentArgs.fromBundle(it).id
                compositeDisposable.add(
                    artDao.getArtById(selectedId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleResponseOldArts)
                )
            }

        }


            // Selecting image operations
        binding.imageView.setOnClickListener{

            galleryPermission()

        }

            // Creating new 'Art' operations
        binding.createButton.setOnClickListener {

            val artName = binding.artNameText.text.toString()
            val artistName = binding.artistNameText.text.toString()
            val year = binding.yearText.text.toString()
                // Getting datas

            if (selectedBitmap != null) {
                val smallerBitmap = makeSmallerBitmap(selectedBitmap!! , 350)
                    // Create bitmap

                val outputStream = ByteArrayOutputStream()
                    // Create ByteArray class

                smallerBitmap.compress(Bitmap.CompressFormat.PNG , 50 , outputStream)
                val selectedImage = outputStream.toByteArray()
                    // Convert 'smallerBitmap' to 'ByteArray'

                val art = Art(artName , artistName , year , selectedImage)

                compositeDisposable.add(
                    artDao.insert(art)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleResponse)
                )
                    // RoomDatabase instert operations
            }

        }

            // Delete data in recycleView operations
        binding.deleteButton.setOnClickListener {

            isArtNull?.let {
                compositeDisposable.add(
                    artDao.delete(it)
                        .observeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleResponse)
                )
            }

        }


    }

    private fun galleryPermission() {

        if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            // If version > TIRAMISU (+33) ---> READ_MEDIA_IMAGES

            if (ContextCompat.checkSelfPermission(requireContext() , Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                // Permission Denied by the User --> Rationale --> Request Permission

                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity() , Manifest.permission.READ_MEDIA_IMAGES)) {
                    // Snackbar --->

                    Snackbar.make(requireView() , "Permission needed for gallery!" , Snackbar.LENGTH_INDEFINITE).setAction("GIVE PERMISSION" , View.OnClickListener {
                        // Request Permission

                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)

                    }).show()
                } else {
                    // Request Permission

                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)

                }
            } else {
                // Permission Granted ---> Intent to gallery

                val intentToGallery = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }

        } else {
            // Version < TIRAMISU (-33) ---> READ_EXTERNAL_STORAGE

            if (ContextCompat.checkSelfPermission(requireContext() , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Permission Denied by the User --> Rationale --> Request Permission

                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity() , Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Snackbar --->

                    Snackbar.make(requireView() , "Permission needed for gallery!" , Snackbar.LENGTH_INDEFINITE).setAction("GIVE PERMISSION" , View.OnClickListener {
                        // Request Permission

                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                    }).show()
                } else {
                    // Request Permission

                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                }
            } else {
                // Permission Granted ---> Intent to gallery

                val intentToGallery = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }

    }

    private fun registerLauncher() {

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
            // Start activity for result --->

            if (result.resultCode == RESULT_OK) {
                // intent?
                val intentFromResult = result.data

                if (intentFromResult != null) {
                    //Uri?
                    val imageData = intentFromResult.data

                    if (imageData != null) {
                        if (VERSION.SDK_INT >= 28) {
                            // Version +28 --->

                            try {
                                val source = ImageDecoder.createSource(requireContext().contentResolver , imageData)
                                selectedBitmap = ImageDecoder.decodeBitmap(source)
                                binding.imageView.setImageBitmap(selectedBitmap)

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            // Version +28 --->
                            selectedBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver , imageData)
                            binding.imageView.setImageBitmap(selectedBitmap)

                        }
                    }
                }
            }
        }


        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {result ->

            if (result) {
                // Permission Granted
                val intentToGallery = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                // Permission Denied
                Toast.makeText(requireContext() , "Permission needed!" , Toast.LENGTH_LONG).show()
            }

        }

    }

    @SuppressLint("UseKtx")
    private fun makeSmallerBitmap(image: Bitmap, maximumSize: Int) : Bitmap {

        var width = image.width
        var height = image.height

        val bitmapRatio : Double = width.toDouble() / height.toDouble()

        if (bitmapRatio > 1) {
            // Image = Landscape
            width = maximumSize
            val scaledHeight = width / bitmapRatio
            height = scaledHeight.toInt()

        } else {
            // Image = Portrait
            height = maximumSize
            val scaledHeight = height * bitmapRatio
            width = scaledHeight.toInt()
        }

        val versionControl =
            if (VERSION.SDK_INT >= 30) {
                image.scale(width , height)
            } else Bitmap.createScaledBitmap(image , width , height , true)

        return versionControl

    }

    private fun handleResponse() {

        val action = UploadFragmentDirections.actionUploadFragmentToArtFragment()
        requireView().findNavController().navigate(action)

    }

    private fun handleResponseOldArts(art: Art) {

        isArtNull = art
        binding.artNameText.setText(art.artName)
        binding.artistNameText.setText(art.artistName)
        binding.yearText.setText(art.year)
        art.image?.let {
            val bitmap = BitmapFactory.decodeByteArray(it , 0 , it.size)
            binding.imageView.setImageBitmap(bitmap)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}