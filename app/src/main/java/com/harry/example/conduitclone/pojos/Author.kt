package com.harry.example.conduitclone.pojos

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Author(
    @SerializedName("following")
    var isFollowing: Boolean = false,
    @SerializedName("image")
    val imageUrl: String = "",
    @SerializedName("username")
    val username: String = ""
) : Parcelable {
    constructor(source: Parcel) : this(
        1 == source.readInt(),
        source.readString()!!,
        source.readString()!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt((if (isFollowing) 1 else 0))
        writeString(imageUrl)
        writeString(username)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Author> = object : Parcelable.Creator<Author> {
            override fun createFromParcel(source: Parcel): Author = Author(source)
            override fun newArray(size: Int): Array<Author?> = arrayOfNulls(size)
        }
    }
}