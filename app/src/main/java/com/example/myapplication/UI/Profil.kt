package com.example.myapplication.UI

import ApiConfig
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.Preferences.SharedPreference
import com.example.myapplication.databinding.ActivityProfilBinding
import profileResponse
import profileResult
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
        val Userid = sharedPreference.getUserId().toString()
        val authorizationHeader = "Bearer $token"

        print("Token : $token")
        print("Userid : $Userid")
        GetProfile(authorizationHeader,Userid)
        binding.inputEmailProfile.setText(sharedPreference.getUserEmail())
        binding.displayNamaProfile.setText(sharedPreference.getUserName())
    }
    private fun GetProfile(token:String,Userid:String) {
        val client = ApiConfig().getApiService().getUserProfile(token, Userid)
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
}