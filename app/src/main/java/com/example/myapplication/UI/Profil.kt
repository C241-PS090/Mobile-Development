package com.example.myapplication.UI

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication.Preferences.SharedPreference
import com.example.myapplication.R
import com.example.myapplication.ViewModel.ProfileManager
import com.example.myapplication.databinding.ActivityProfilBinding
import profileResponse

class Profil : AppCompatActivity(), ProfileManager.ProfileCallback {

    private lateinit var binding: ActivityProfilBinding
    private lateinit var sharedPreference: SharedPreference
    private lateinit var profileManager: ProfileManager
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreference = SharedPreference(this)
        profileManager = ProfileManager(this)

        val token = sharedPreference.getUserToken().toString()
        val userId = sharedPreference.getUserId().toString()
        val authorizationHeader = "Bearer $token"

        profileManager.getProfile(authorizationHeader, userId, this)
        binding.inputEmailProfile.text = sharedPreference.getUserEmail()
        binding.displayNamaProfile.text = sharedPreference.getUserName()

        val imagerUrl = sharedPreference.getImageProfile()
        imageView = binding.imageView2

        println("Image URL: $imagerUrl")

        if (imagerUrl != null && imagerUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imagerUrl)
                .circleCrop()
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.profile)
        }

        binding.bottomNavigationView.selectedItemId = R.id.profile_button
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_button -> {
                    startActivity(Intent(this, Beranda::class.java))
                    true
                }
                R.id.camera_button -> {
                    startActivity(Intent(this, Scan::class.java))
                    true
                }
                R.id.profile_button -> true
                else -> false
            }
        }

        binding.updateProfile.setOnClickListener {
            startActivity(Intent(this, UbahProfil::class.java))
        }
        binding.LogOut.setOnClickListener {
            showLogoutDialog()
        }
    }

    override fun onSuccess(profileResponse: profileResponse) {
        sharedPreference.saveUserName(profileResponse.data.name)
        sharedPreference.saveUserEmail(profileResponse.data.email)
        binding.displayNamaProfile.text = profileResponse.data.name
        binding.inputEmailProfile.text = profileResponse.data.email
    }

    override fun onError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun showLogoutDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.popup_dialog)

        val yesButton: Button = dialog.findViewById(R.id.yesButton)
        val noButton: Button = dialog.findViewById(R.id.noButton)

        yesButton.setOnClickListener {
            dialog.dismiss()
            profileManager.logout()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        noButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
