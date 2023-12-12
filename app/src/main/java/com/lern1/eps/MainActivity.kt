package com.lern1.eps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity() ,OnMapReadyCallback{
    private lateinit var mGoogleMap:GoogleMap
    private var locationArrayList: ArrayList<LatLng>?=null

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

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap=googleMap

        for(i in locationArrayList!!.indices){
            mGoogleMap.addMarker(MarkerOptions().position(locationArrayList!![i]).title("Marker"))
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationArrayList!!.get(i),15f))


        }
    }
}