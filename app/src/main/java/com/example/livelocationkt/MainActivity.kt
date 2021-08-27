package com.example.livelocationkt

import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {


    var smf: SupportMapFragment? = null
    var client: FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        smf = supportFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment?
        client = LocationServices.getFusedLocationProviderClient(this)


        Dexter.withContext(applicationContext)
                .withPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {

                        getMyLocation()

                    }

                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {

                    }

                    override fun onPermissionRationaleShouldBeShown(p0: PermissionRequest?, p1: PermissionToken?) {

                        if (p1 != null) {
                            p1.continuePermissionRequest()
                        }
                    }

                }).check()








    }

    private fun getMyLocation() {


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        val task = client?.lastLocation


        //~~~~~~~~~~~~~~~~~~~By this code we can get location after open google map--------
//        Task<Location> task = client.getLastLocation();
        task!!.addOnSuccessListener { location ->
            smf!!.getMapAsync { googleMap ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    val latLng = LatLng(location.latitude, location.longitude)
                    val markerOptions = MarkerOptions().position(latLng).title("Your are here..")
                    googleMap.addMarker(markerOptions)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 25f))
                    val geocoder: Geocoder
                    var addresses: List<Address>? = null
                    geocoder = Geocoder(this@MainActivity, Locale.getDefault())
                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    val address = addresses!![0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    val city = addresses!![0].locality
                    val state = addresses!![0].adminArea
                    val country = addresses!![0].countryName
                    val postalCode = addresses!![0].postalCode
                    val knownName = addresses!![0].featureName
                    val add = "$address,$city,$state,$country,$postalCode"
                    val textView = findViewById<View>(R.id.tvaddress) as TextView
                    textView.text = add
                    Toast.makeText(this@MainActivity, "$address,$city,$state,$country,$postalCode", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Location not found ", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }
}