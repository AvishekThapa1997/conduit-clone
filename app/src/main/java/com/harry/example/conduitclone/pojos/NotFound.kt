package com.harry.example.conduitclone.pojos

import com.google.gson.annotations.SerializedName

data class NotFound(
    @SerializedName("error")
    val error: String
)