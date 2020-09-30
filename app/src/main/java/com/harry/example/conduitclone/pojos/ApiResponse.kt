package com.harry.example.conduitclone.pojos

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("article") val article: Article?,
    @SerializedName("articles") val articles: List<Article>? = null
)
