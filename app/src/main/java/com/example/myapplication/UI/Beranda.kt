package com.example.myapplication.UI

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityBerandaBinding

class Beranda : AppCompatActivity() {
    private lateinit var binding: ActivityBerandaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBerandaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up bottom navigation
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_button -> {
                    // Do nothing, we are already on the home screen
                    true
                }
                R.id.camera_button -> {
                    startActivity(Intent(this, Scan::class.java))
                    true
                }
                R.id.profile_button -> {
                    startActivity(Intent(this, Profil::class.java))
                    true
                }
                else -> false
            }
        }
    }

}