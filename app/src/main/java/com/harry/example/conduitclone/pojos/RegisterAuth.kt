package com.harry.example.conduitclone.pojos


import com.google.gson.annotations.SerializedName

data class RegisterAuth(
    @SerializedName("user") val user: User,
    @SerializedName("errors") val credentialError: CredentialError?
) {
    constructor(user: User) : this(user, null)
}
