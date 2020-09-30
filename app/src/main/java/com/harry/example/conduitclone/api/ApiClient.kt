package com.harry.example.conduitclone.api

import com.harry.example.conduitclone.pojos.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiClient {
    @GET("api/articles")
    suspend fun getAllArticles(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("author") authorName: String = "",
    ): Response<ApiResponse>

//    @GET("api/articles/{slug}/comments")
//    suspend fun getAllComments(slug: String?): Response<CommentResponse?>

    @GET("api/user")
    suspend fun currentUser(@Header("Authorization") token: String): Response<LoginAuth>

    @POST("api/users")
    suspend fun registerUser(@Body registerCredentials: RegisterAuth): Response<RegisterAuth>

    @POST("/api/users/login")
    suspend fun loginUser(@Body loginCredentials: LoginAuth): Response<LoginAuth>

    @POST("api/articles/{slug}/favorite")
    suspend fun favouriteArticle(
        @Header("Authorization") token: String,
        @Path("slug") slug: String?
    ): Response<ApiResponse>

    @DELETE("api/articles/{slug}/favorite")
    suspend fun unFavouriteArticle(
        @Header("Authorization") token: String,
        @Path("slug") slug: String?
    ): Response<ApiResponse>

    @POST("/api/profiles/{username}/follow")
    suspend fun followUser(
        @Header("Authorization") token: String,
        @Path("username") author_username: String
    ): Response<Profile?>

    @DELETE("/api/profiles/{username}/follow")
    suspend fun unfollowUser(
        @Header("Authorization") token: String,
        @Path("username") author_username: String
    ): Response<Profile>

    @POST("/api/articles")
    suspend fun postArticle(
        @Header("Authorization") token: String,
        @Body newArticle: PostArticle
    ): Response<PostArticle>

    @DELETE("/api/articles/{slug}")
    suspend fun deleteArticle(
        @Header("Authorization") token: String,
        @Path("slug") slug: String
    ): Response<ResponseBody>

    @PUT("/api/articles/{slug}")
    suspend fun updateArticle(
        @Header("Authorization") token: String,
        @Path("slug") slug: String,
        @Body updateArticle: PostArticle
    ): Response<PostArticle>


    @PUT("/api/user")
    suspend fun uploadPhoto(
        @Header("Authorization") token: String,
        @Body loginCredentials: LoginAuth,
    ): Response<LoginAuth>
}