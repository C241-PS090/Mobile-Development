package com.example.myapplication.UI

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.Image.ImagePicker

import com.example.myapplication.databinding.ActivityUbahProfilBinding

class UbahProfil : AppCompatActivity() {
    private lateinit var binding: ActivityUbahProfilBinding
    private lateinit var imagePicker: ImagePicker
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUbahProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imagePicker = ImagePicker(this, binding.imageUpdate, application, "com.example.myapplication.fileprovider", activityResultRegistry)
        imagePicker.initialize()

        binding.cameraIcon.setOnClickListener {
            imagePicker.startGallery()
        }

    }
}