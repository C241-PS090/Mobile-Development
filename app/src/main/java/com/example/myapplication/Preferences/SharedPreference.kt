package com.example.myapplication.Preferences

import android.content.Context
import android.content.SharedPreferences

class SharedPreference(context: Context) {
    private val login = "login"
    private val myPref = "Main_pref"
    private val myToken = "Bearer"
    private val myName = "Name"
    private val myEmail = "Email"
    private val myId = "userId"
    private val myImage = "Image"
    private val myAge = "Age"
    private val myGender = "Gender"
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
        return sharedPreference.getString(myToken, "")
    }

    fun saveUserId(id: String) {
        sharedPreference.edit().putString(myId, id).apply()
    }

    fun getUserId(): String? {
        return sharedPreference.getString(myId, "")
    }

    fun clearUserToken() {
        sharedPreference.edit().remove(myToken).apply()
    }

    fun clearUserLogin() {
        sharedPreference.edit().remove(login).apply()
    }

    fun saveUserName(name: String) {
        sharedPreference.edit().putString(myName, name).apply()
    }

    fun getUserName(): String? {
        return sharedPreference.getString(myName, "")
    }

    fun saveUserEmail(email: String) {
        sharedPreference.edit().putString(myEmail, email).apply()
    }

    fun getUserEmail(): String? {
        return sharedPreference.getString(myEmail, "")
    }

    fun clearUserName() {
        sharedPreference.edit().remove(myName).apply()
    }

    fun setImageProfile(profilePictureUrl: String) {
        sharedPreference.edit().putString(myImage, profilePictureUrl).apply()
    }

    fun getImageProfile(): String? {
        return sharedPreference.getString(myImage, "")
    }

    fun saveAge(age: String?) {
        sharedPreference.edit().putString(myAge, age).apply()
    }
    fun getAge(): String? {
        return sharedPreference.getString(myAge, "")
    }

    fun saveGender(Gender: String?) {
        sharedPreference.edit().putString(myGender, Gender).apply()
    }
    fun getGender(): String? {
        return sharedPreference.getString(myGender, "")
    }

    fun clearAllUserData() {
        sharedPreference.edit().clear().apply()
    }
}
