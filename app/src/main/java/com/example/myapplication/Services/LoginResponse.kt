package com.example.myapplication.Services

import com.google.gson.annotations.SerializedName

data class LoginResult(

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("token")
    val token: String
)