package com.example.myapplication.UI

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMasukBinding
import com.google.firebase.auth.FirebaseAuth

class Masuk : AppCompatActivity() {
    private lateinit var binding: ActivityMasukBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        binding = ActivityMasukBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.BackButtonLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.Login.setOnClickListener {
            val email = binding.inputEmailLogin.text.toString()
            val password = binding.inputPasswordLogin.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, Profil::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Tolong lengkapi Formnya Dahulu", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
