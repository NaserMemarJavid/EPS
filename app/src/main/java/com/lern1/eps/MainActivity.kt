package com.lern1.eps

import android.app.Instrumentation.ActivityResult
import android.content.IntentSender
import android.content.pm.PackageManager
import android.icu.text.Transliterator
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mGoogleMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private val permissionCode = 101
    private var currentLocation: Location? = null

    val mark1=LatLng(51.447734,7.269780)
    val mark2=LatLng(51.446996,7.270313)
    val mark3=LatLng(51.446741,7.271077)
    val mark4=LatLng(51.447157,7.272354)
    val mark5=LatLng(51.447656,7.273599)
    val mark6=LatLng(51.448662,7.272676)
    val mark7=LatLng(51.448194,7.271131)

    private val locations = mutableListOf<Location>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        createLocationRequest()
        checkLocationSettings()
        locations.addAll(loadLocationsFromFile())
        val btnHighAccuracy  = findViewById<Button>(R.id.btnHighAccuracy)
        val btnBalancedPower = findViewById<Button>(R.id.btnBalancedPower)
        val btnLowPower      = findViewById<Button>(R.id.btnLowPower)
        val btnSaveLocations = findViewById<Button>(R.id.save_button)

        btnHighAccuracy.setOnClickListener {
            setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        }

        btnBalancedPower.setOnClickListener {
            setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
        }

        btnLowPower.setOnClickListener {
            setPriority(LocationRequest.PRIORITY_LOW_POWER)
        }

        btnSaveLocations.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    permissionCode
                )
            } else {
                val locationListener: LocationListener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        locations.add(location)
                        Log.i("LocationLog", "Breitengrad: ${location.latitude}, Längengrad: ${location.longitude}")


                        fusedLocationProviderClient.removeLocationUpdates(this)


                        saveLocationsToFile()

                        
                        Log.i("LocationLog", "Alle Standorte:")
                        for (i in locations.indices) {
                            val loc = locations[i]
                            Log.i("LocationLog", "Standort $i - Breitengrad: ${loc.latitude}, Längengrad: ${loc.longitude}")
                        }
                    }
                }
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationListener, null)
            }
        }







    }
    private fun saveLocationsToFile() {
        try {
            val file = File(filesDir, "locations.txt")
            val fileOutputStream = FileOutputStream(file)
            val objectOutputStream = ObjectOutputStream(fileOutputStream)

            // Wandele Location-Objekte in SerializableLocation-Objekte um
            val serializableLocations = locations.map {
                SerializableLocation(it.latitude, it.longitude)
            }

            // Schreibe die SerializableLocation-Objekte in die Datei
            objectOutputStream.writeObject(serializableLocations)

            objectOutputStream.close()
            fileOutputStream.close()

            Toast.makeText(this, "Standorte erfolgreich gespeichert", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("SaveLocations", "Fehler beim Speichern der Standorte: ${e.message}")
            Toast.makeText(this, "Fehler beim Speichern der Standorte", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadLocationsFromFile(): MutableList<Location> {
        try {
            val file = File(filesDir, "locations.txt")
            if (!file.exists()) {
                Log.i("LoadLocations", "Die Datei locations.txt existiert nicht.")
                return mutableListOf()
            }

            val fileInputStream = FileInputStream(file)
            val objectInputStream = ObjectInputStream(fileInputStream)

            // Lese SerializableLocation-Objekte aus der Datei
            val serializableLocations = objectInputStream.readObject() as List<SerializableLocation>

            objectInputStream.close()
            fileInputStream.close()

            // Wandele SerializableLocation-Objekte in Location-Objekte um
            val loadedLocations = serializableLocations.map {
                Location("").apply {
                    latitude = it.latitude
                    longitude = it.longitude
                }
            }.toMutableList()


            Log.i("LoadLocations", "Erfolgreich ${loadedLocations.size} Standorte geladen.")


            for (i in 0 until loadedLocations.size) {
                val loc = loadedLocations[i]
                Log.i("LoadLocations", "Standort $i - Breitengrad: ${loc.latitude}, Längengrad: ${loc.longitude}")
            }

            return loadedLocations
        } catch (e: Exception) {
            Log.e("LoadLocations", "Fehler beim Laden der Standorte: ${e.message}")
            return mutableListOf()
        }
    }




    private fun setPriority(priority: Int) {
        locationRequest.priority = priority
        checkLocationSettings()
    }
    private fun checkLocationSettings() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            getLastKnownLocation()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(this@MainActivity, 1)
                } catch (sendEx: IntentSender.SendIntentException) {
                    sendEx.printStackTrace()
                }
            }
        }
    }
    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                permissionCode
            )
            return
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                onLocationChanged(location)
            }
        }
    }

    private fun onLocationChanged(location: Location) {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000
            fastestInterval = 5000
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionCode && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLastKnownLocation()
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        currentLocation?.let {
            val latLng = LatLng(it.latitude, it.longitude)
            val markerOption = MarkerOptions().position(latLng).title("Current Location")
            googleMap.addMarker(markerOption)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }

        // Hier werden die Marker hinzugefügt
        val marker1 = MarkerOptions().position(mark1).title("Marker 1")
        val marker2 = MarkerOptions().position(mark2).title("Marker 2")
        val marker3 = MarkerOptions().position(mark3).title("Marker 1")
        val marker4 = MarkerOptions().position(mark4).title("Marker 2")
        val marker5 = MarkerOptions().position(mark5).title("Marker 1")
        val marker6 = MarkerOptions().position(mark6).title("Marker 2")
        val marker7 = MarkerOptions().position(mark7).title("Marker 1")


        googleMap.addMarker(marker1)
        googleMap.addMarker(marker2)
        googleMap.addMarker(marker3)
        googleMap.addMarker(marker4)
        googleMap.addMarker(marker5)
        googleMap.addMarker(marker6)
        googleMap.addMarker(marker7)


        for (i in locations.indices) {
            val location = locations[i]
            val latLng = LatLng(location.latitude, location.longitude)
            val markerOptions = MarkerOptions().position(latLng).title("Gespeicherter Standort $i")
            googleMap.addMarker(markerOptions)
        }


    }
}


