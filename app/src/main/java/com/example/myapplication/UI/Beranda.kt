package com.example.myapplication.UI

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.Preferences.SharedPreference
import com.example.myapplication.R
import com.example.myapplication.ViewModel.ProfileManager
import com.example.myapplication.databinding.ActivityBerandaBinding
import profileResponse

class Beranda : AppCompatActivity(), ProfileManager.ProfileCallback {

    private lateinit var sharedPreference: SharedPreference
    private lateinit var binding: ActivityBerandaBinding
    private lateinit var profileManager: ProfileManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBerandaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreference = SharedPreference(this)
        profileManager = ProfileManager(this)

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
            nearestHospital()
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
        Toast.makeText(this, "Get profile successful: ${profileResponse.message}", Toast.LENGTH_SHORT).show()
    }

    override fun onError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun nearestHospital() {
        val gmmIntentUri = Uri.parse("geo:0,0?q=hospital")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        } else {
            Toast.makeText(this, "Google Maps app not found", Toast.LENGTH_SHORT).show()
        }
    }
}
