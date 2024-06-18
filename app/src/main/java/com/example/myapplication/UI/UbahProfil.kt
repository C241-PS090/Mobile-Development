package com.example.myapplication.UI

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import com.example.myapplication.databinding.ActivityUbahProfilBinding

class UbahProfil : AppCompatActivity() {
    private lateinit var binding: ActivityUbahProfilBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUbahProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}