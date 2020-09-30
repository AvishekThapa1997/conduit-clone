package com.harry.example.conduitclone.pojos

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class User(
    @SerializedName("username")
    val username: String,
    @SerializedName("token")
    @Expose(serialize = false, deserialize = true)
    val token: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("bio")
    val bio: String,
    @SerializedName("image")
    var image: String?

) : Serializable {
    constructor(email: String, username: String, password: String) : this(
        username = username,
        email = email,
        password = password,
        bio = "",
        image = "",
        token = ""
    )

    constructor(email: String, password: String) : this(
        email = email,
        password = password,
        username = "",
        bio = "",
        image = "",
        token = ""
    )
}