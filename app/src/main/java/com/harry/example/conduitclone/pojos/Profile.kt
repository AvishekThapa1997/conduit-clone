package com.harry.example.conduitclone.pojos

import com.google.gson.annotations.SerializedName

data class Profile(@SerializedName("profile") val authorProfile: Author?, val notFound: NotFound?)