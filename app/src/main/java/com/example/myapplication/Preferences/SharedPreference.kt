package com.example.myapplication.Preferences

import android.content.Context
import android.content.SharedPreferences

class SharedPreference(context: Context) {
    private val login = "login"
    private val myPref = "Main_pref"
    private val myToken = "Bearer"
    private val myName = "Name"
    private val myEmail = "Email"
    private val myid = "userId"
    private val sharedPreference: SharedPreferences

    init {
        sharedPreference = context.getSharedPreferences(myPref, Context.MODE_PRIVATE)
    }

    fun setStatusLogin(status: Boolean) {
        sharedPreference.edit().putBoolean(login, status).apply()
    }

    fun getStatusLogin(): Boolean {
        return sharedPreference.getBoolean(login, false)
    }

    fun saveUserToken(token: String) {
        sharedPreference.edit().putString(myToken, token).apply()
    }

    fun getUserToken(): String? {
        return sharedPreference.getString(myToken, " ")
    }
    fun saveUserId(id: String) {
        sharedPreference.edit().putString(myid, id).apply()
    }

    fun getUserId(): String? {
        return sharedPreference.getString(myid, " ")
    }

    fun clearUserToken() {
        sharedPreference.edit().remove(myToken).apply()
    }

    fun clearUserLogin() {
        sharedPreference.edit().remove(login).apply()
    }

    // Fungsi untuk menyimpan nama pengguna
    fun saveUserName(name: String) {
        sharedPreference.edit().putString(myName, name).apply()
    }

    // Fungsi untuk mengambil nama pengguna
    fun getUserName(): String? {
        return sharedPreference.getString(myName, " ")
    }

    // Fungsi untuk menyimpan email pengguna
    fun saveUserEmail(email: String) {
        sharedPreference.edit().putString(myEmail, email).apply()
    }

    // Fungsi untuk mengambil email pengguna
    fun getUserEmail(): String? {
        return sharedPreference.getString(myEmail, " ")
    }

    // Fungsi untuk membersihkan nama pengguna
    fun clearUserName() {
        sharedPreference.edit().remove(myName).apply()
    }

    // Fungsi untuk membersihkan email pengguna
    fun clearUserEmail() {
        sharedPreference.edit().remove(myEmail).apply()
    }

    // Fungsi untuk membersihkan semua data pengguna
    fun clearAllUserData() {
        sharedPreference.edit().clear().apply()
    }
}
