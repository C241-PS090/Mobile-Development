package com.example.myapplication.UI

import ApiConfig
import LoginResponse
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMasukBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Masuk : AppCompatActivity() {
    private lateinit var binding: ActivityMasukBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMasukBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.BackButtonLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.Login.setOnClickListener {
            val email = binding.inputEmailLogin.text.toString().trim()
            val password = binding.inputPasswordLogin.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                login(email, password)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun login(email: String, password: String) {
        val client = ApiConfig().getApiService().login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        Toast.makeText(this@Masuk, "Login successful: ${responseBody.message}", Toast.LENGTH_SHORT).show()
                        // Navigate to the next activity
                        val intent = Intent(this@Masuk, Scan::class.java)
                        startActivity(intent)
                        finish() // Optional: close this activity
                    } else {
                        Toast.makeText(this@Masuk, "Login failed: No response body", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@Masuk, "Login failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@Masuk, "Login failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
