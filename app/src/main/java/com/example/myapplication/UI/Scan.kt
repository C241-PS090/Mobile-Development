package com.example.myapplication.UI

import ApiConfig
import PredictResponse
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.Image.ImagePicker
import com.example.myapplication.Preferences.SharedPreference
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityScanBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class Scan : AppCompatActivity() {
    private lateinit var sharedPreference: SharedPreference
    private lateinit var binding: ActivityScanBinding
    private lateinit var imagePicker: ImagePicker
    private var progressDialog: Dialog? = null // Dialog loading

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreference = SharedPreference(this)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imagePicker = ImagePicker(this, binding.imagePreview, application, "com.example.myapplication.fileprovider", activityResultRegistry)
        imagePicker.initialize()

        binding.ambilFoto.setOnClickListener {
            if (imagePicker.checkCameraPermission()) {
                imagePicker.startCamera()
            } else {
                imagePicker.requestCameraPermission()
            }
        }

        binding.galeri.setOnClickListener {
            imagePicker.startGallery()
        }

        binding.scanButton.setOnClickListener {
            val file = imagePicker.getFile()
            val userId = sharedPreference.getUserId().toString()
            val userIdWithoutQuotes = userId.removeSurrounding("\"")
            if (file != null) {
                showProgressDialog()
                Predict(file, userIdWithoutQuotes)
            } else {
                Toast.makeText(this, "File is null", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showProgressDialog() {
        progressDialog = Dialog(this)
        progressDialog?.setContentView(R.layout.loading_scan)
        progressDialog?.setCancelable(false) // Prevent dismiss on outside touch
        progressDialog?.show()
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }

    private fun Predict(file: File, userId: String) {
        val upFile = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody("multipart/form-data".toMediaTypeOrNull()))
        val userIdRequestBody = userId.toRequestBody("text/plain".toMediaTypeOrNull())

        val client = ApiConfig().getPredictApiService().Predict(upFile, userIdRequestBody)

        client.enqueue(object : Callback<PredictResponse> {
            override fun onResponse(call: Call<PredictResponse>, response: Response<PredictResponse>) {
                dismissProgressDialog() // Dismiss loading dialog
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        var predict_class = responseBody.predictions.predictionClass
                        val Confidance = responseBody.predictions.confidence
                        val formattedConfidence = String.format("%.2f", Confidance)
                        var pesan = ""

                        if (predict_class == "Abnormal(Ulcer)"){
                            predict_class = "Ulcer"
                            pesan = "Cek Gula Darah Anda!"
                        } else if (predict_class == "Normal(Healthy skin)"){
                            predict_class = "Normal"
                            pesan = "Kulit Anda Sehat!"
                        } else if(predict_class == "Wound Images"){
                            predict_class = "Luka Non-Ulcer"
                            pesan = "Obati Luka Anda!"
                        }
                        showPredictionDialog(predict_class + " " + formattedConfidence + "%", pesan)
                    } else {
                        Toast.makeText(this@Scan, "Predict failed: No response body", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@Scan, "Predict failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PredictResponse>, t: Throwable) {
                dismissProgressDialog() // Dismiss loading dialog
                Toast.makeText(this@Scan, "Predict failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showPredictionDialog(prediction: String, pesan: String) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.popup_hasil, null)

        val alertIcon = dialogLayout.findViewById<ImageView>(R.id.alertIcon)
        val hasilText = dialogLayout.findViewById<TextView>(R.id.Hasil)
        val pesanText = dialogLayout.findViewById<TextView>(R.id.tips)
        val yesButton = dialogLayout.findViewById<Button>(R.id.yesButton)

        hasilText.text = prediction
        pesanText.text = pesan

        if (pesan == "Cek Gula Darah Anda!") {
            alertIcon.setImageResource(R.drawable.warning)
        } else if (pesan == "Kulit Anda Sehat!") {
            alertIcon.setImageResource(R.drawable.ceklis)
        } else {
            alertIcon.setImageResource(R.drawable.warning_yellow)
        }

        builder.setView(dialogLayout)
        val dialog = builder.create()
        dialog.show()

        yesButton.setOnClickListener {
            val intent = Intent(this, Beranda::class.java)
            startActivity(intent)
        }
    }
}