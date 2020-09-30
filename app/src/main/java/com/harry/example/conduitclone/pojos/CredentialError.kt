package com.harry.example.conduitclone.pojos

import com.google.gson.annotations.SerializedName

data class CredentialError(
    @SerializedName("username") val usernameErrors: List<String>?,
    @SerializedName("email") val emailErrors: List<String>?,
    @SerializedName("password") val passwordErrors: List<String>?,
    @SerializedName("email or password") val loginError: List<String>?
)