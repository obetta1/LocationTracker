package com.example.locationtracker
//
//package com.example.locationtracker
//
//import android.Manifest
//import android.content.pm.PackageManager
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.widget.Toast
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//
//import com.google.android.gms.maps.CameraUpdateFactory
//import com.google.android.gms.maps.GoogleMap
//import com.google.android.gms.maps.OnMapReadyCallback
//import com.google.android.gms.maps.SupportMapFragment
//import com.google.android.gms.maps.model.LatLng
//import com.google.android.gms.maps.model.MarkerOptions
//import com.example.locationtracker.databinding.ActivityMapsBinding
//import com.google.android.gms.location.*
//import com.google.android.gms.maps.model.BitmapDescriptorFactory
//import com.google.android.material.floatingactionbutton.FloatingActionButton
//import com.google.firebase.FirebaseApp
//import com.google.firebase.database.*
//import com.google.firebase.database.core.Context
//import com.google.firebase.database.ktx.database
//import com.google.firebase.ktx.Firebase
//
////class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
//
//    private lateinit var mMap: GoogleMap
//    private lateinit var binding: ActivityMapsBinding
//    private val LOCATION_PERMISSION_REQUEST = 1
//    private lateinit var fusedLocationClient: FusedLocationProviderClient
//    private lateinit var locationRequest: LocationRequest
//    private lateinit var locationCallback: LocationCallback
//    lateinit var databaseRef: DatabaseReference
//
//    //to access mile location
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMapsBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        databaseRef = FirebaseDatabase.getInstance().reference
//
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        val mapFragment = supportFragmentManager
//            .findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)
//        //
//        fusedLocationClient =  LocationServices.getFusedLocationProviderClient(this)
//    }
//
//
//
//    private fun getLocationAccess(){
//        //check if the user location permission has been granted
//        // if otherwise request for permission to access users location
//        if(ContextCompat.checkSelfPermission(this,
//                android.Manifest.permission.ACCESS_FINE_LOCATION) ==
//            PackageManager.PERMISSION_GRANTED){
//            mMap.isMyLocationEnabled = true
//            getLocationUpdates()
//            Toast.makeText(this, "Location Permission granted", Toast.LENGTH_LONG).show()
//            startLocationUpdates()
//        }else {
//            ActivityCompat.requestPermissions(this,
//                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
//                LOCATION_PERMISSION_REQUEST)
//        }
//    }
//
//
//
//    // fetching location from firebase on location path
//    val logListener = object : ValueEventListener {
//
//
//
//        //using datasnapshot to get the data from firebase
//        override fun onDataChange(dataSnapshot: DataSnapshot) {
//
//            if (dataSnapshot.exists()) {
//                val locationlogging = dataSnapshot.child("").getValue(UserDataClass::class.java)
//                val driverLat=locationlogging?.latitude
//                val driverLong=locationlogging?.longitude
//
//                if (driverLat !=null  && driverLong != null) {
//                    val location = LatLng(driverLat, driverLong)
//                    val markerOptions = MarkerOptions().position(location).title("Anthony is here")
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.anthony__2_))
//                    mMap.clear()
//                    mMap.addMarker(markerOptions)
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
//                    //Zoom level - 1: World, 5: Landmass/continent, 10: City, 15: Streets and 20: Buildings
//                }
//            }
//        }
//
//        override fun onCancelled(error: DatabaseError) {
//            TODO("Not yet implemented")
//        }
//    }
//    //
//    private fun getLocationUpdates() {
//        locationRequest = LocationRequest()
//        locationRequest.interval = 10000
//        locationRequest.fastestInterval = 2000
//        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//
//        locationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult) {
//                //passing the co-ordinates to firebase
//                if (locationResult.locations.isNotEmpty()) {
//
//                    val location = locationResult.lastLocation
//                    databaseRef = FirebaseDatabase.getInstance().reference
//                    // writing data to database
//                    val locationlogging = UserDataClass(location.latitude, location.longitude)
//                    databaseRef.addValueEventListener(logListener)
//                    Toast.makeText(this@MapsActivity, "Sending location to database", Toast.LENGTH_LONG).show()
//
//                    databaseRef.child("UserDataClass").setValue(locationlogging)
//                        .addOnSuccessListener {
//
//                            Toast.makeText(this@MapsActivity, "Success", Toast.LENGTH_LONG).show()
//
//                        }
//                        .addOnFailureListener {
//                            Toast.makeText(applicationContext, "Error occured while writing the locations", Toast.LENGTH_LONG).show()
//                        }
//
//
//                    val latLng = LatLng(location.latitude, location.longitude)
//                    val markerOptions = MarkerOptions().position(latLng)
//                    mMap.addMarker(markerOptions)
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
//                }
//            }
//        }
//    }
//
//
//    private fun startLocationUpdates() {
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        )
//
//            fusedLocationClient.requestLocationUpdates(
//                locationRequest,
//                locationCallback,
//                null
//            )
//
//        fusedLocationClient.lastLocation.addOnCompleteListener {
//            val location = it.result
//            val locationlogging = UserDataClass(location.latitude, location.longitude)
//            databaseRef.child("UserDataClass").setValue(locationlogging)
//
//            // Toast.makeText(this@MapsActivity, "Sending location to database " +
//            //      "${location.latitude}, ${location.longitude}", Toast.LENGTH_LONG).show()
//
//
//
//        }
//
//    }
//
//
//
//    /**
//     * Manipulates the map once available.
//     * This callback is triggered when the map is ready to be used.
//     * This is where we can add markers or lines, add listeners or move the camera. In this case,
//     * we just add a marker near Sydney, Australia.
//     * If Google Play services is not installed on the device, the user will be prompted to install
//     * it inside the SupportMapFragment. This method will only be triggered once the user has
//     * installed Google Play services and returned to the app.
//     */
//    override fun onMapReady(googleMap: GoogleMap) {
//        mMap = googleMap
//
//        getLocationAccess()
//        //startLocationUpdates()
//        //getLocationUpdates()
//
//
//        // Add a marker in Sydney and move the camera
////        val sydney = LatLng(-34.0, 151.0)
////        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
////        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
//    }
//}