package com.example.myapplication.Image

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.myapplication.Image.createCustomTempFile
import java.io.File

class ImagePicker(
    private val activity: Activity,
    private val imageView: ImageView,
    private val application: Context,
    private val fileProviderAuthority: String,
    private val registry: ActivityResultRegistry
) {
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null

    fun getFile(): File? = getFile

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var launcherIntentCamera: ActivityResultLauncher<Intent>
    private lateinit var launcherIntentGallery: ActivityResultLauncher<Intent>

    fun initialize() {
        requestPermissionLauncher = registry.register("requestPermission", ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                startCamera()
            } else {
                Toast.makeText(application, "Anda harus memberikan akses untuk melanjutkan", Toast.LENGTH_SHORT).show()
            }
        }

        launcherIntentCamera = registry.register("launcherIntentCamera", ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val myFile = File(currentPhotoPath)
                getFile = myFile
                val resultBitmap = BitmapFactory.decodeFile(myFile.path)
                imageView.setImageBitmap(resultBitmap)
            }
        }

        launcherIntentGallery = registry.register("launcherIntentGallery", ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri: Uri? = result.data?.data
                if (selectedImageUri != null) {
                    val tempFile = createCustomTempFile(application)
                    val inputStream = application.contentResolver.openInputStream(selectedImageUri)
                    tempFile.outputStream().use { outputStream ->
                        inputStream?.copyTo(outputStream)
                    }
                    getFile = tempFile
                    val resultBitmap = BitmapFactory.decodeFile(tempFile.path)
                    imageView.setImageBitmap(resultBitmap)
                }
            }
        }
    }

    fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestCameraPermission() {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(application.packageManager)
        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                application,
                fileProviderAuthority,
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        launcherIntentGallery.launch(intent)
    }
}