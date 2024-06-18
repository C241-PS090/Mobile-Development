package com.example.myapplication.UI

import ApiConfig
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.Preferences.SharedPreference
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityProfilBinding
import profileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Profil : AppCompatActivity() {

    private lateinit var binding: ActivityProfilBinding
    private lateinit var sharedPreference: SharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreference = SharedPreference(this)

        val token = sharedPreference.getUserToken().toString()
        val userId = sharedPreference.getUserId().toString()
        val authorizationHeader = "Bearer $token"

        print("Token: $token")
        print("UserId: $userId")

        GetProfile(authorizationHeader, userId)
        binding.inputEmailProfile.text = sharedPreference.getUserEmail()
        binding.displayNamaProfile.text = sharedPreference.getUserName()

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
            startActivity(Intent(this, UbahProfil::class.java))}
        binding.LogOut.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun GetProfile(token: String, userId: String) {
        val client = ApiConfig().getApiService().getUserProfile(token, userId)
        client.enqueue(object : Callback<profileResponse> {
            override fun onResponse(
                call: Call<profileResponse>,
                response: Response<profileResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        sharedPreference.saveUserName(responseBody.data.name)
                        sharedPreference.saveUserEmail(responseBody.data.email)
                        binding.displayNamaProfile.text = responseBody.data.name
                        binding.inputEmailProfile.text = responseBody.data.email
                        Toast.makeText(
                            this@Profil,
                            "Get profile successful: ${responseBody.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@Profil,
                            "Get profile failed: No response body",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@Profil,
                        "Get profile failed: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<profileResponse>, t: Throwable) {
                Toast.makeText(
                    this@Profil,
                    "Get profile failed: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun showLogoutDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.popup_dialog)

        val yesButton: Button = dialog.findViewById(R.id.yesButton)
        val noButton: Button = dialog.findViewById(R.id.noButton)

        yesButton.setOnClickListener {
            dialog.dismiss()
            performLogout()
        }

        noButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun performLogout() {
        sharedPreference.setStatusLogin(false)
        sharedPreference.clearUserToken()
        sharedPreference.clearUserLogin()
        sharedPreference.clearUserName()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

