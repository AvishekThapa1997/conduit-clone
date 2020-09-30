package com.harry.example.conduitclone.pojos

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

//@Entity(tableName = "articles", indices = arrayOf(Index(value = ["slug"], unique = true)))
data class Article(

//    @ColumnInfo(name = "articleId")
//    @PrimaryKey(autoGenerate = true)
//    @Expose(serialize = false, deserialize = false)
//    var articleId: Long,

    //@ColumnInfo(name = "slug")
    @SerializedName("slug")
    val slug: String?,

    @SerializedName("author")
    //@Embedded(prefix = "_author")
    val author: Author?,

    @SerializedName("body")
    //@ColumnInfo(name = "article_content")
    val articleContent: String?,

    @SerializedName("description")
    //@ColumnInfo(name = "description")
    val description: String?,

    @SerializedName("favorited")
    //@ColumnInfo(name = "favorited")
    var isFavourited: Boolean = false,

    @SerializedName("favoritesCount")
    //@ColumnInfo(name = "favorites_count")
    var favoritesCount: Int = 0,

    @SerializedName("tagList")
    //@ColumnInfo(name = "tags")
    val tagList: List<String>?,

    @SerializedName("title")
    //@ColumnInfo(name = "title")
    val title: String?,

    @SerializedName("createdAt")
    //@ColumnInfo(name = "createdAt")
    var createdAt: String?,

    @SerializedName("updatedAt")
    val updatedAt : String?

) : Parcelable {
    constructor(
        title: String?,
        description: String?,
        articleContent: String?,
        tagList: List<String>?
    ) : this(null, null, articleContent, description, false, 0, tagList, title, null,null)

    constructor(source: Parcel) : this(
        source.readString(),
        source.readParcelable<Author>(Author::class.java.classLoader),
        source.readString(),
        source.readString(),
        1 == source.readInt(),
        source.readInt(),
        source.createStringArrayList(),
        source.readString(),
        source.readString(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(slug)
        writeParcelable(author, 0)
        writeString(articleContent)
        writeString(description)
        writeInt((if (isFavourited) 1 else 0))
        writeInt(favoritesCount)
        writeStringList(tagList)
        writeString(title)
        writeString(createdAt)
        writeString(updatedAt)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Article> = object : Parcelable.Creator<Article> {
            override fun createFromParcel(source: Parcel): Article = Article(source)
            override fun newArray(size: Int): Array<Article?> = arrayOfNulls(size)
        }
    }
}
