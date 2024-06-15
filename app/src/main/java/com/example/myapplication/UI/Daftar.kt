package com.example.myapplication.UI

import ApiConfig
import SignUpResponse
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityDaftarBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Daftar : AppCompatActivity() {
    private lateinit var binding: ActivityDaftarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDaftarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.BackButtonDaftar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.daftarBtn.setOnClickListener {
            val name = binding.inputNama.text.toString().trim()
            val email = binding.inputEmail.text.toString().trim()
            val password = binding.inputPassword.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                signUp(name, email, password)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }

            val intent = Intent(this,Masuk::class.java)
            startActivity(intent)
        }
    }

    private fun signUp(name: String, email: String, password: String) {
        val client = ApiConfig().getApiService().register(name, email, password)
        client.enqueue(object : Callback<SignUpResponse> {
            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        Toast.makeText(this@Daftar, "Sign-up successful: ${responseBody.message}", Toast.LENGTH_SHORT).show()
                        // Navigate to the login activity
                        val intent = Intent(this@Daftar, Masuk::class.java)
                        startActivity(intent)
                        finish() // Optional: close this activity
                    } else {
                        Toast.makeText(this@Daftar, "Sign-up failed: No response body", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@Daftar, "Sign-up failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                Toast.makeText(this@Daftar, "Sign-up failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
