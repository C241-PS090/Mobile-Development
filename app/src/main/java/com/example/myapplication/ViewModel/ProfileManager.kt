package com.example.myapplication.ViewModel

import android.content.Context
import android.widget.Toast
import com.example.myapplication.Preferences.SharedPreference
import profileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ApiConfig

class ProfileManager(private val context: Context) {

    private val sharedPreference: SharedPreference = SharedPreference(context)

    fun getProfile(token: String, userId: String, callback: ProfileCallback) {
        val client = ApiConfig().getApiService().getUserProfile(token, userId)
        client.enqueue(object : Callback<profileResponse> {
            override fun onResponse(call: Call<profileResponse>, response: Response<profileResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        sharedPreference.saveUserName(responseBody.data.name)
                        sharedPreference.saveUserEmail(responseBody.data.email)
                        sharedPreference.setImageProfile(responseBody.data.profilePictureUrl)
                        callback.onSuccess(responseBody)
                    } else {
                        callback.onError("Get profile failed: No response body")
                    }
                } else {
                    callback.onError("Get profile failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<profileResponse>, t: Throwable) {
                callback.onError("Get profile failed: ${t.message}")
            }
        })
    }

    fun logout() {
        sharedPreference.setStatusLogin(false)
        sharedPreference.clearUserToken()
        sharedPreference.clearUserLogin()
        sharedPreference.clearUserName()
        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
    }

    interface ProfileCallback {
        fun onSuccess(profileResponse: profileResponse)
        fun onError(errorMessage: String)
    }
}