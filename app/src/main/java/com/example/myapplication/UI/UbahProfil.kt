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
import com.example.myapplication.Preferences.SharedPreference
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
    private lateinit var sharedPreference: SharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUbahProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreference = SharedPreference(this)

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
            val token = sharedPreference.getUserToken().toString()
            val authorizationHeader = "Bearer $token"
            val userId = sharedPreference.getUserId().toString()
            val Nama = binding.NameUpdate.text.toString().trim()
            val Gender = genderSpinner.selectedItem.toString() // Get selected gender
            val Umur = binding.Umur.text.toString().trim()
            val Profilepicture = imagePicker.getFile()
            if (Profilepicture != null) {
                UpdateProfile(authorizationHeader, userId, Nama, Gender, Umur, Profilepicture)
                intent = Intent(this, Profil::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "File is null", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun UpdateProfile(token: String, userId: String, Nama: String, Gender: String, Umur: String, Profilepicture: File) {
        val profilePicturePart = MultipartBody.Part.createFormData(
            "profilePicture",
            Profilepicture.name,
            Profilepicture.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        )

        val client = ApiConfig().getApiService().updateUserProfile(token, userId, Nama, Gender, Umur, profilePicturePart)
        client.enqueue(object : Callback<UpdateProfileResponse> {
            override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        sharedPreference.saveUserName(responseBody.data.name)
                        sharedPreference.saveGender(responseBody.data.gender)
                        sharedPreference.saveAge(responseBody.data.age)
                        sharedPreference.setImageProfile(responseBody.data.profilePictureUrl ?: "")
                        Toast.makeText(this@UbahProfil, "Update Profile successful: ${responseBody.message}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@UbahProfil, "Update Profile failed: No response body", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@UbahProfil, "Update Profile failed 1: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                Toast.makeText(this@UbahProfil, "Update Profile failed 2: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
