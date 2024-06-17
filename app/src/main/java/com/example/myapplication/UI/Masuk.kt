package com.example.myapplication.UI

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.Preferences.SharedPreference
import com.example.myapplication.ViewModel.MasukViewModel
import com.example.myapplication.databinding.ActivityMasukBinding

class Masuk : AppCompatActivity() {
    private lateinit var binding: ActivityMasukBinding
    private lateinit var sharedPreference: SharedPreference
    private val masukViewModel: MasukViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMasukBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreference
        sharedPreference = SharedPreference(this)

        binding.BackButtonLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.Login.setOnClickListener {
            val email = binding.inputEmailLogin.text.toString().trim()
            val password = binding.inputPasswordLogin.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                handleLogin(email, password)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        masukViewModel.name.observe(this) { loginResult ->
            if (loginResult != null) {
                // Save login result to SharedPreferences
                sharedPreference.setStatusLogin(true)
                sharedPreference.saveUserToken(loginResult.token)
                sharedPreference.saveUserName(loginResult.name)
                sharedPreference.saveUserEmail(loginResult.email)

                val intent = Intent(this, Profil::class.java)
                intent.putExtra("NAME", loginResult.name)
                intent.putExtra("EMAIL", loginResult.email)
                intent.putExtra("TOKEN", loginResult.token)
                startActivity(intent)
            } else {
                Log.d("Masuk", "nama is null")
            }
        }
    }

    private fun handleLogin(email: String, password: String) {
        masukViewModel.login(email, password)
    }
}
