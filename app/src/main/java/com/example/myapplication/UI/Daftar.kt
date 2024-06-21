package com.example.myapplication.UI

import ApiConfig
import SignUpResponse
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityDaftarBinding
import com.example.myapplication.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Daftar : AppCompatActivity() {
    private lateinit var binding: ActivityDaftarBinding
    private lateinit var loadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDaftarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the loading dialog
        loadingDialog = createLoadingDialog()

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
        }
    }

    private fun signUp(name: String, email: String, password: String) {
        // Show the loading dialog when sign-up is initiated
        showLoadingDialog()

        val client = ApiConfig().getApiService().register(name, email, password)
        client.enqueue(object : Callback<SignUpResponse> {
            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                // Dismiss the loading dialog when the response is received
                hideLoadingDialog()

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
                // Dismiss the loading dialog when the request fails
                hideLoadingDialog()
                Toast.makeText(this@Daftar, "Sign-up failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createLoadingDialog(): Dialog {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.loading)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    private fun showLoadingDialog() {
        if (!loadingDialog.isShowing) {
            loadingDialog.show()
        }
    }

    private fun hideLoadingDialog() {
        if (loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
    }
}
