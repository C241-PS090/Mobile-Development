package com.example.myapplication.ViewModel

import ApiConfig
import LoginResponse
import LoginResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log

class MasukViewModel : ViewModel() {
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _isLoginError = MutableLiveData<Boolean>()
    val isLoginError: LiveData<Boolean> = _isLoginError

    private val _isLogin = MutableLiveData<LoginResponse>()
    val isLogin: LiveData<LoginResponse> = _isLogin

    private val _name = MutableLiveData<LoginResult>()
    val name: LiveData<LoginResult> = _name

    private val _test = MutableLiveData<String>()
    val test: LiveData<String> = _test

    fun login(email: String, password: String) {
        val client = ApiConfig().getApiService().login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("MasukViewModel", "Response body: $responseBody")
                    if (responseBody != null) {
                        _test.value = responseBody.message
                        _name.value = responseBody.data
                        _isLogin.value = responseBody
                        _isLoginError.value = false
                    } else {
                        _isLoginError.value = true
                        _error.value = "Login failed: No response body"
                    }
                } else {
                    _isLoginError.value = true
                    _error.value = "Login failed: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoginError.value = true
                _error.value = "Login failed: ${t.message}"
            }
        })
    }
}
