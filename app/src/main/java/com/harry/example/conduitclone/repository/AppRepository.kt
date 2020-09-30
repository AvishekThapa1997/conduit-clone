package com.harry.example.conduitclone.repository


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.harry.example.conduitclone.api.ApiClient
import com.harry.example.conduitclone.pojos.*
import com.harry.example.conduitclone.utility.*
import org.koin.core.KoinComponent
import kotlin.Exception

class AppRepository(private val apiClient: ApiClient) : KoinComponent {
    //private val articleDao: ArticleDao by inject()

    var offset = 0


    @Throws(Exception::class)
    suspend fun getAllArticles(token: String, authorName: String = ""): ApiResponse? {
        val response = apiClient.getAllArticles(token, 20, this.offset, authorName)
        if (response.isSuccessful) {
            this.offset += 20
            return response.body()
        }
        return null
    }


    @Throws(Exception::class)
    suspend fun registerUser(registerCredentials: RegisterAuth): RegisterAuth? {
        val res = apiClient.registerUser(registerCredentials)
        return when (res.code()) {
            SUCCESS_CODE -> res.body()
            VALIDATION_ERROR_CODE -> Gson().fromJson<RegisterAuth>(
                res.errorBody()?.charStream(), object : TypeToken<RegisterAuth>() {}.type
            )
            else -> null
        }
    }

    @Throws(Exception::class)
    suspend fun loginUser(loginCredential: LoginAuth): LoginAuth? {
        val response = apiClient.loginUser(loginCredential)
        return when (response.code()) {
            SUCCESS_CODE -> response.body()
            VALIDATION_ERROR_CODE -> Gson().fromJson<LoginAuth>(
                response.errorBody()?.charStream(),
                object : TypeToken<LoginAuth>() {}.type
            )
            else -> null
        }
    }

    @Throws(Exception::class)
    suspend fun currentUser(token: String): User? {
        val response = apiClient.currentUser(token)
        if (response.isSuccessful)
            return response.body()?.user
        return null
    }

    @Throws(Exception::class)
    suspend fun favouriteArticle(token: String, slug: String?): ApiResponse? {
        val res = apiClient.favouriteArticle(token, slug)
        if (res.isSuccessful) {
            return res.body()
        }
        return null
    }

    @Throws(Exception::class)
    suspend fun unFavouriteArticle(token: String, slug: String?): ApiResponse? {
        val res = apiClient.unFavouriteArticle(token, slug)
        if (res.isSuccessful) {
            return res.body()
        }
        return null
    }


    @Throws(Exception::class)
    suspend fun followUser(token: String, authorName: String): Profile? {
        val response = apiClient.followUser(token, authorName)
        if (response.isSuccessful)
            return response.body()
        return null
    }

    @Throws(Exception::class)
    suspend fun unFollowUser(token: String, authorName: String): Profile? {
        val response = apiClient.unfollowUser(token, authorName)
        if (response.isSuccessful)
            return response.body()
        return null
    }

    @Throws(Exception::class)
    suspend fun postArticle(token: String, postArticle: PostArticle): Article? {
        val response = apiClient.postArticle(token, postArticle)
        if (response.isSuccessful)
            return response.body()?.newArticle
        return null
    }

    @Throws(Exception::class)
    suspend fun deleteArticle(token: String, slug: String): Boolean {
        val response = apiClient.deleteArticle(token, slug)
        if (response.isSuccessful)
            return true
        return false
    }

    @Throws(Exception::class)
    suspend fun updateArticle(token: String, slug: String, postArticle: PostArticle): Article? {
        val response = apiClient.updateArticle(token, slug, postArticle)
        if (response.isSuccessful)
            return response.body()?.newArticle
        return null
    }
}