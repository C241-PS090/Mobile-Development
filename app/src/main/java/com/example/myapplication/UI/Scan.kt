package com.example.myapplication.UI

import ApiConfig
import PredictResponse
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultRegistry
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.Image.ImagePicker
import com.example.myapplication.Preferences.SharedPreference
import com.example.myapplication.databinding.ActivityScanBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class Scan : AppCompatActivity() {
    private lateinit var sharedPreference: SharedPreference
    private lateinit var binding: ActivityScanBinding
    private lateinit var imagePicker: ImagePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreference = SharedPreference(this)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imagePicker = ImagePicker(this, binding.imagePreview, application, "com.example.myapplication.fileprovider", activityResultRegistry)
        imagePicker.initialize()

        binding.ambilFoto.setOnClickListener {
            if (imagePicker.checkCameraPermission()) {
                imagePicker.startCamera()
            } else {
                imagePicker.requestCameraPermission()
            }
        }

        binding.galeri.setOnClickListener {
            imagePicker.startGallery()
        }

        binding.scanButton.setOnClickListener {
            val file = imagePicker.getFile()
            val userId = sharedPreference.getUserId().toString()
            if (file != null) {
                Predict(file, userId)
            } else {
                Toast.makeText(this, "File is null", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun Predict(file: File, userId: String) {
        val upFile = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody("multipart/form-data".toMediaTypeOrNull()))
        val client = ApiConfig().getPredictApiService().Predict(upFile, userId)

        client.enqueue(object : Callback<PredictResponse> {
            override fun onResponse(call: Call<PredictResponse>, response: Response<PredictResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        Toast.makeText(this@Scan, "Predict successful: ${responseBody}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@Scan, "Predict failed: No response body", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@Scan, "Predict failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PredictResponse>, t: Throwable) {
                Toast.makeText(this@Scan, "Predict failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
