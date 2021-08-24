package com.example.locationtracker

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.locationtracker.databinding.ActivityMapsBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.database.core.Context
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val LOCATION_PERMISSION_REQUEST = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    lateinit var databaseRef: DatabaseReference

    //to access mile location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

       databaseRef = FirebaseDatabase.getInstance().reference

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        //
        fusedLocationClient =  LocationServices.getFusedLocationProviderClient(this)
    }


    private fun getLocationAccess(){
        if(ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED){
            //map.isMyLocationEnabled = true
            getLocationUpdates()
            startLocationUpdates()
        }else {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST)
        }
    }


    //opens the map if allowed, toast a message otherwise. works with the getLocationAccess()
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST){
            if(grantResults.contains(PackageManager.PERMISSION_GRANTED)){
                getLocationAccess()
            }else{
                Toast.makeText(this, "Permission needs to be granted for this app to be functional", Toast.LENGTH_LONG).show()
//                finish()
            }
        }
    }



    //fetching location from firebase on location path
    val logListener = object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(applicationContext, "Could not read from database", Toast.LENGTH_SHORT).show()
        }

        //using datasnapshot to get the data from firebase
        override fun onDataChange(dataSnapshot: DataSnapshot) {

            if (dataSnapshot.exists()) {
       // read location data from partners device
                val locationlogging = dataSnapshot.child("locationAnthony").getValue(UserDataClass::class.java)
                val driverLat=locationlogging?.latitude
                val driverLong=locationlogging?.longitude
                if (driverLat !=null  && driverLong != null) {
                    val driverLoc = LatLng(driverLat, driverLong)
      Toast.makeText(applicationContext, "latitude  $driverLat", Toast.LENGTH_LONG).show()
      Toast.makeText(applicationContext, "longitude  $driverLong", Toast.LENGTH_LONG).show()
                    val markerOptions = MarkerOptions().position(driverLoc).title("Anthony id here")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.anthony__2_))
                    mMap.addMarker(markerOptions)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(driverLoc, 15f))


                }
            }
        }
    }

    //to get the location every 5 seconds
    private fun getLocationUpdates() {
        locationRequest = LocationRequest()
        locationRequest.interval = 8_000
        locationRequest.fastestInterval = 5_000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult.locations.isNotEmpty()) {
                    val location = locationResult.lastLocation

       //writing the co-ordinates of device current location  to firebase
                    databaseRef = Firebase.database.reference
                    val locationlogging = UserDataClass(location.latitude, location.longitude)
                    databaseRef.addValueEventListener(logListener)
                    databaseRef.child("UserDataClass").setValue(locationlogging)
                        .addOnSuccessListener {

                        }
                        .addOnFailureListener {
                            Toast.makeText(applicationContext, "Error occured while writing the locations", Toast.LENGTH_LONG).show()
                        }

                }
            }
        }
    }




    private fun startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )

//        fusedLocationClient.lastLocation.addOnCompleteListener {
//            val location = it.result
//            val locationlogging = UserDataClass(location.latitude, location.longitude)
//            databaseRef.child("UserDataClass").setValue(locationlogging)
//
//
//        }

    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

       getLocationAccess()
      startLocationUpdates()
        //getLocationUpdates()


        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}