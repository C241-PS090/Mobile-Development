package com.example.myapplication.UI

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.Preferences.SharedPreference
import com.example.myapplication.R
import com.example.myapplication.ViewModel.ProfileManager
import com.example.myapplication.databinding.ActivityBerandaBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import profileResponse

class Beranda : AppCompatActivity(), ProfileManager.ProfileCallback {

    private lateinit var sharedPreference: SharedPreference
    private lateinit var binding: ActivityBerandaBinding
    private lateinit var profileManager: ProfileManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBerandaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreference = SharedPreference(this)
        profileManager = ProfileManager(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.userName.text = sharedPreference.getUserName()

        // Set up bottom navigation
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_button -> {
                    // Do nothing, we are already on the home screen
                    true
                }

                R.id.camera_button -> {
                    startActivity(Intent(this, Scan::class.java))
                    true
                }

                R.id.profile_button -> {
                    startActivity(Intent(this, Profil::class.java))
                    true
                }

                else -> false
            }
        }
        binding.clinicBtn.setOnClickListener {
            checkLocationPermission()
        }
        binding.quizBtn.setOnClickListener {
            startActivity(Intent(this, Quiz::class.java))
        }

        val token = sharedPreference.getUserToken().toString()
        val userId = sharedPreference.getUserId().toString()
        val authorizationHeader = "Bearer $token"

        profileManager.getProfile(authorizationHeader, userId, this)

        binding.userName.text = sharedPreference.getUserName()
    }

    override fun onSuccess(profileResponse: profileResponse) {
        sharedPreference.saveUserName(profileResponse.data.name)
        sharedPreference.saveUserEmail(profileResponse.data.email)
        binding.userName.text = profileResponse.data.name
        Toast.makeText(
            this,
            "Get profile successful: ${profileResponse.message}",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            getCurrentLocationAndShowHospitals()
        }
    }

    private fun getCurrentLocationAndShowHospitals() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        showHospitalsNearby(it)
                    } ?: run {
                        Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun showHospitalsNearby(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude
        val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?z=10&q=hospital")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getCurrentLocationAndShowHospitals()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
