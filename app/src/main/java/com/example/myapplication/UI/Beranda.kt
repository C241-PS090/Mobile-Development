package com.example.myapplication.UI

import ApiConfig
import HistoryPredictResponse
import Prediction
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.Preferences.SharedPreference
import com.example.myapplication.R
import com.example.myapplication.ViewModel.AdapterHistory
import com.example.myapplication.ViewModel.ProfileManager
import com.example.myapplication.databinding.ActivityBerandaBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import profileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Beranda : AppCompatActivity(), ProfileManager.ProfileCallback {

    private lateinit var sharedPreference: SharedPreference
    private lateinit var binding: ActivityBerandaBinding
    private lateinit var profileManager: ProfileManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var imageView: ImageView

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBerandaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi SharedPreference, ProfileManager, dan FusedLocationClient
        sharedPreference = SharedPreference(this)
        profileManager = ProfileManager(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Setup profile information
        binding.userName.text = sharedPreference.getUserName()
        binding.Umur.text = sharedPreference.getAge()
        binding.Gender.text = sharedPreference.getGender()

        // Setup profile image
        val imageUrl = sharedPreference.getImageProfile()
        imageView = binding.profileImage
        if (imageUrl != null && imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .circleCrop()
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.profile)
        }

        // Setup bottom navigation
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_button -> true
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

        // Setup buttons
        binding.clinicBtn.setOnClickListener {
            checkLocationPermission()
        }
        binding.quizBtn.setOnClickListener {
            startActivity(Intent(this, Quiz::class.java))
        }
        binding.tipsBtnImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.halodoc.com/artikel/search/diabetes"))
            startActivity(intent)
        }

        // Get user profile and history predictions
        val token = sharedPreference.getUserToken().toString()
        val userId = sharedPreference.getUserId().toString()
        val authorizationHeader = "Bearer $token"

        profileManager.getProfile(authorizationHeader, userId, this)
        GetHistoryPredict(userId)
    }



    override fun onSuccess(profileResponse: profileResponse) {
        sharedPreference.saveUserName(profileResponse.data.name)
        sharedPreference.saveUserEmail(profileResponse.data.email)
        sharedPreference.setImageProfile(profileResponse.data.profilePictureUrl ?: "")
        binding.userName.text = profileResponse.data.name
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

    private fun GetHistoryPredict(userId: String) {
        val client = ApiConfig().getApiService().getHistoryPredictions(userId)

        client.enqueue(object : Callback<HistoryPredictResponse> {
            override fun onResponse(call: Call<HistoryPredictResponse>, response: Response<HistoryPredictResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        setupRecyclerView(responseBody.data)
                    } else {
                        Toast.makeText(this@Beranda, "Hist Predict failed: No response body", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@Beranda, "Hist Predict failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<HistoryPredictResponse>, t: Throwable) {
                Toast.makeText(this@Beranda, "Hist Predict failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupRecyclerView(predictions: List<Prediction>) {
        val recyclerView = findViewById<RecyclerView>(R.id.rv_stories)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = AdapterHistory(predictions)
        recyclerView.adapter = adapter
    }

}
