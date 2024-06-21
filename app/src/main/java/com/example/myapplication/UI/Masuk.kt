package com.example.myapplication.UI

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.Preferences.SharedPreference
import com.example.myapplication.ViewModel.MasukViewModel
import com.example.myapplication.databinding.ActivityMasukBinding
import com.example.myapplication.R

class Masuk : AppCompatActivity() {
    private lateinit var binding: ActivityMasukBinding
    private lateinit var sharedPreference: SharedPreference
    private val masukViewModel: MasukViewModel by viewModels()
    private lateinit var loadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMasukBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreference
        sharedPreference = SharedPreference(this)

        // Initialize the loading dialog
        loadingDialog = createLoadingDialog()

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
            // Dismiss the loading dialog when the response is received
            hideLoadingDialog()

            if (loginResult != null) {
                // Save login result to SharedPreferences
                sharedPreference.setStatusLogin(true)
                sharedPreference.saveUserToken(loginResult.token)
                sharedPreference.saveUserId(loginResult.userId)
                val intent = Intent(this, Beranda::class.java)
                startActivity(intent)
            } else {
                Log.d("Masuk", "nama is null")
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleLogin(email: String, password: String) {
        // Show the loading dialog when login is initiated
        showLoadingDialog()
        masukViewModel.login(email, password)
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
