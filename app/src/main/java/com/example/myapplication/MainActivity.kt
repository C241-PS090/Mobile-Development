package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.daftarBtnSplash.setOnClickListener {
            val intent = Intent(this,Daftar::class.java)
            startActivity(intent)
        }

        binding.masukBtnSplsh.setOnClickListener {
            val intent = Intent(this,Masuk::class.java)
            startActivity(intent)
        }
    }
}