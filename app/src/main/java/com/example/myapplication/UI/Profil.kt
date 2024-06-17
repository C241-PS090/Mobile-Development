package com.example.myapplication.UI

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.Preferences.SharedPreference
import com.example.myapplication.databinding.ActivityProfilBinding

class Profil : AppCompatActivity() {

    private lateinit var binding: ActivityProfilBinding
    private lateinit var sharedPreference: SharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreference = SharedPreference(this)

        val name = sharedPreference.getUserName()
        val email = sharedPreference.getUserEmail()

        // Set the retrieved data to the TextViews
        binding.displayNamaProfile.text = name
        binding.inputEmailProfile.text = email
    }
}
