package com.lern1.eps

import android.app.Instrumentation.ActivityResult
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity() ,OnMapReadyCallback{
    private lateinit var mGoogleMap:GoogleMap
    private var locationArrayList: ArrayList<LatLng>?=null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentLocation: Location
    private val permissionCode=101

    val mark1=LatLng(51.447734,7.269780)
    val mark2=LatLng(51.446996,7.270313)
    val mark3=LatLng(51.446741,7.271077)
    val mark4=LatLng(51.447157,7.272354)
    val mark5=LatLng(51.447656,7.273599)
    val mark6=LatLng(51.448662,7.272676)
    val mark7=LatLng(51.448194,7.271131)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       /* val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        locationArrayList = ArrayList()
        locationArrayList!!.add(mark1)
        locationArrayList!!.add(mark2)
        locationArrayList!!.add(mark3)
        locationArrayList!!.add(mark4)
        locationArrayList!!.add(mark5)
        locationArrayList!!.add(mark6)
        locationArrayList!!.add(mark7) */
        fusedLocationProviderClient =LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocationUser()

    }
    private fun getCurrentLocationUser(){
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),permissionCode)
            return
        }
        val getLocation=fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            location ->
            if(location != null){
                currentLocation = location
                Toast.makeText(applicationContext,currentLocation.latitude.toString()+""+
                currentLocation.longitude.toString(),Toast.LENGTH_LONG).show()

                val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.mapFragment) as SupportMapFragment
                mapFragment.getMapAsync(this)
                locationArrayList = ArrayList()
                locationArrayList!!.add(mark1)
                locationArrayList!!.add(mark2)
                locationArrayList!!.add(mark3)
                locationArrayList!!.add(mark4)
                locationArrayList!!.add(mark5)
                locationArrayList!!.add(mark6)
                locationArrayList!!.add(mark7)

            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            permissionCode -> if(grantResults.isNotEmpty()&& grantResults[0]==
                PackageManager.PERMISSION_GRANTED){
                getCurrentLocationUser()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap=googleMap
        val latLng=LatLng(currentLocation.latitude,currentLocation.longitude)
        val markerOption = MarkerOptions().position(latLng).title("Current Location")
        googleMap?.addMarker(markerOption)
        for(i in locationArrayList!!.indices){
            mGoogleMap.addMarker(MarkerOptions().position(locationArrayList!![i]).title("Marker"))
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationArrayList!!.get(i),15f))


        }
    }
}