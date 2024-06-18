package com.example.myapplication.UI

import ApiConfig
import PredictResponse
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.myapplication.Image.createCustomTempFile
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
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private lateinit var binding: ActivityScanBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreference = SharedPreference(this)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ambilFoto.setOnClickListener {
            if (checkCameraPermission()) {
                startCamera()
            } else {
                requestCameraPermission()
            }
        }

        binding.galeri.setOnClickListener {
            startGallery()
        }


        binding.scanButton.setOnClickListener {
            val file = getFile
            val userId = sharedPreference.getUserId().toString()
            if (file != null) {
                Predict(file, userId)
            } else {
                Toast.makeText(this, "File is null", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(this, "Anda harus memberikan akses untuk melanjutkan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestCameraPermission() {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "com.example.myapplication.fileprovider",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val resultBitmap = BitmapFactory.decodeFile(myFile.path)
            binding.imagePreview.setImageBitmap(resultBitmap)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImageUri: Uri? = result.data?.data
            if (selectedImageUri != null) {
                val tempFile = createCustomTempFile(application)
                val inputStream = contentResolver.openInputStream(selectedImageUri)
                tempFile.outputStream().use { outputStream ->
                    inputStream?.copyTo(outputStream)
                }
                getFile = tempFile
                val resultBitmap = BitmapFactory.decodeFile(tempFile.path)
                binding.imagePreview.setImageBitmap(resultBitmap)
            }
        }
    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        launcherIntentGallery.launch(intent)
    }

    private fun Predict(file: File, UserID: String) {
        if (file == null) {
            Toast.makeText(this, "File is null", Toast.LENGTH_SHORT).show()
            return
        }
        val upFile = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody("multipart/form-data".toMediaTypeOrNull()))
        val client = ApiConfig().getPredictApiService().Predict(upFile, UserID)

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
                    Toast.makeText(this@Scan, "Predict failed 2: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PredictResponse>, t: Throwable) {
                Toast.makeText(this@Scan, "Predict failed 3: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
