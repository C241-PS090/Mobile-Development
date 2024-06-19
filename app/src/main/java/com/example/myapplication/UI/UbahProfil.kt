package com.example.myapplication.UI

import ApiConfig
import UpdateProfileResponse
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.Image.ImagePicker
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityUbahProfilBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UbahProfil : AppCompatActivity() {
    private lateinit var binding: ActivityUbahProfilBinding
    private lateinit var imagePicker: ImagePicker
    private lateinit var genderSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUbahProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imagePicker = ImagePicker(this, binding.imageUpdate, application, "com.example.myapplication.fileprovider", activityResultRegistry)
        imagePicker.initialize()

        // Initialize the spinner
        genderSpinner = binding.genderSpinner
        val adapter = ArrayAdapter.createFromResource(this,
            R.array.gender_options, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner.adapter = adapter

        binding.cameraIcon.setOnClickListener {
            imagePicker.startGallery()
        }

        binding.updateProfile.setOnClickListener {
            val token = "Bearer " + intent.getStringExtra("token")
            val userId = intent.getStringExtra("userId").toString()
            val Nama = binding.NameUpdate.text.toString().trim()
            val Gender = genderSpinner.selectedItem.toString() // Get selected gender
            val Umur = binding.Umur.text.toString().trim()

            // Assuming you have a valid file for Profilepicture
            val Profilepicture = File("path/to/profile/picture")

            UpdateProfile(token, userId, Nama, Gender, Umur, Profilepicture)
        }
    }

    private fun UpdateProfile(token: String, userId: String, Nama: String, Gender: String, Umur: String, Profilepicture: File) {
        val upFile = MultipartBody.Part.createFormData("ProfilePicture", Profilepicture.name, Profilepicture.asRequestBody("multipart/form-data".toMediaTypeOrNull()))
        val client = ApiConfig().getApiService().updateUserProfile(token, userId, Nama, Gender, Umur, upFile)
        client.enqueue(object : Callback<UpdateProfileResponse> {
            override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        Toast.makeText(this@UbahProfil, "Update Profile successful: ${responseBody.message}", Toast.LENGTH_SHORT).show()
                        // Navigate to the login activity
                        val intent = Intent(this@UbahProfil, Masuk::class.java)
                        startActivity(intent)
                        finish() // Optional: close this activity
                    } else {
                        Toast.makeText(this@UbahProfil, "Update Profile failed: No response body", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@UbahProfil, "Update Profile failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                Toast.makeText(this@UbahProfil, "Update Profile failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
