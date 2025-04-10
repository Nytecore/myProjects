package com.example.travelbook.view

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.example.travelbook.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.travelbook.databinding.ActivityMapsBinding
import com.example.travelbook.model.Place
import com.example.travelbook.roomdb.PlaceDao
import com.example.travelbook.roomdb.PlaceDatabase
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MapsActivity : AppCompatActivity(), OnMapReadyCallback , GoogleMap.OnMapLongClickListener{

    private lateinit var mMap : GoogleMap
    private lateinit var binding : ActivityMapsBinding
    private lateinit var locationManager : LocationManager
    private lateinit var locationListener : LocationListener
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    private lateinit var sharedPreferences : SharedPreferences
    private var trackBoolean : Boolean? = null
    private var selectedLatitude : Double? = null
    private var selectedLongitude : Double? = null
    private lateinit var db : PlaceDatabase
    private lateinit var placeDao : PlaceDao
    val compositeDisposable = CompositeDisposable()
    var placeFromMain : Place? = null
    private var isGpsAvailable = false
    private var isGpsEnabled = false
    private var isNetworkEnabled = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager

        isGpsAvailable = locationManager.allProviders.contains(LocationManager.GPS_PROVIDER)
        isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        registerLauncher()

        sharedPreferences = this.getSharedPreferences("com.example.travelbook" , MODE_PRIVATE)
        trackBoolean = false
        selectedLatitude = 0.0
        selectedLongitude = 0.0


        db = Room.databaseBuilder(applicationContext, PlaceDatabase::class.java, "Places")
            //.allowMainThreadQueries()
            .build()
        placeDao = db.placeDao()

        binding.saveButton.isEnabled = false
            // deactive save button

        binding.saveButton.setOnClickListener{

            if (selectedLatitude != null && selectedLongitude != null) {
                val place = Place (binding.placeText.text.toString(), selectedLatitude!!, selectedLongitude!!)
                compositeDisposable.add(
                    placeDao.insert(place)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleResponse)
                )
            }
        }

        binding.deleteButton.setOnClickListener{

            placeFromMain?.let {
                compositeDisposable.add(
                    placeDao.delete(it)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleResponse)
                )
            }
        }
    }

    private fun handleResponse() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //Close for other all activities
        startActivity(intent)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(this)

        /* latitude ---> enlem
           longitude --> boylam
           Eiffel tower LatLng ---> 48.858378148177025, 2.2949111202794223

        val eiffel = LatLng(48.858378148177025, 2.2949111202794223)
        mMap.addMarker(MarkerOptions().position(eiffel).title("Eiffel Tower"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eiffel , 15f))   */

        val intent = intent
        val info = intent.getStringExtra("info")

        if (info == "new") {

            binding.saveButton.visibility = View.VISIBLE
            binding.deleteButton.visibility = View.GONE

            //casting
            locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager

            locationListener = object : LocationListener {
                override fun onLocationChanged(p0: Location) {
                    trackBoolean = sharedPreferences.getBoolean("trackBoolean" , false)
                    if (!trackBoolean!!) {
                        //trackboolean == false
                        val userLocation = LatLng(p0.latitude , p0.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation , 15f))
                        sharedPreferences.edit().putBoolean("trackBoolean" , true).apply()
                    }
                }
            }

            if (ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this , Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Snackbar.make(binding.root , "Permission needed for location!" , Snackbar.LENGTH_INDEFINITE).setAction("Give Permission") {
                        //request permission
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }.show()
                } else {
                    //request permissions
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            } else {
                //Permission Granted
                if (isGpsAvailable) {
                    if (isGpsEnabled) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER , 0 , 0f , locationListener)
                        val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        if (lastLocation != null) {
                            val lastUserLocation = LatLng(lastLocation.latitude , lastLocation.longitude)
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation , 15f))
                        }
                        mMap.isMyLocationEnabled = true
                    } else {
                        Toast.makeText(this, "GPS not enable" , Toast.LENGTH_LONG).show()
                    }
                } else {
                    if (isNetworkEnabled) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER , 0 , 0f , locationListener)
                        val lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (lastLocation != null) {
                            val lastUserLocation = LatLng(lastLocation.latitude , lastLocation.longitude)
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation , 15f))
                        }
                        mMap.isMyLocationEnabled = true
                    } else {
                        Toast.makeText(this, "Internet not enable" , Toast.LENGTH_LONG).show()
                    }
                }
            }

        } else {

            mMap.clear()
            placeFromMain = intent.getSerializableExtra("selectedPlace") as? Place

            placeFromMain?.let {
                val latlng = LatLng(it.latitude , it.longitude)
                mMap.addMarker(MarkerOptions().position(latlng).title(it.name))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng , 15f))

                binding.placeText.setText(it.name)
                binding.saveButton.visibility = View.GONE
                binding.deleteButton.visibility = View.VISIBLE
            }
        }
    }

    private fun registerLauncher() {

        if (isGpsAvailable) {
            if (isGpsEnabled) {
                permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {result ->
                    if (result) {
                        if (ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            //permission granted
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER , 0 , 0f , locationListener)
                            val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            if (lastLocation != null) {
                                val lastUserLocation = LatLng(lastLocation.latitude , lastLocation.longitude)
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation , 15f))
                            }
                            mMap.isMyLocationEnabled = true
                        }
                    } else {
                        //permission denied
                        Toast.makeText(this@MapsActivity , "Permission denied!", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this , "GPS not enabled" , Toast.LENGTH_LONG).show()
            }
        } else {
            if (isNetworkEnabled) {
                permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {result ->
                    if (result) {
                        if (ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            //permission granted
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER , 0 , 0f , locationListener)
                            val lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                            if (lastLocation != null) {
                                val lastUserLocation = LatLng(lastLocation.latitude , lastLocation.longitude)
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation , 15f))
                            }
                            mMap.isMyLocationEnabled = true
                        }
                    } else {
                        //permission denied
                        Toast.makeText(this@MapsActivity , "Permission denied!", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this , "Internet not enabled" , Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onMapLongClick(p0: LatLng) {
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(p0))
        selectedLatitude = p0.latitude
        selectedLongitude = p0.longitude

        binding.saveButton.isEnabled = true
            // active save button

    }

    override fun onDestroy() {
        super.onDestroy()
            //Clear memory
        compositeDisposable.clear()
    }
}