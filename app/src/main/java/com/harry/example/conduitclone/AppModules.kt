package com.harry.example.conduitclone

import com.harry.example.conduitclone.api.ApiClient
import com.harry.example.conduitclone.repository.AppRepository
import com.harry.example.conduitclone.utility.*
import com.harry.example.conduitclone.viewmodels.*
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val viewModelModules = module {
    viewModel {
        HomeViewModel()
    }
    viewModel{ SharedViewModel() }
    viewModel{ ArticleDetailsViewModel() }
    viewModel{ PostArticleViewModel() }
    viewModel { UserFeedViewModel() }
    viewModel { LoginAuthViewModel() }
    viewModel{ RegisterAuthViewModel() }
    viewModel{ SplashViewModel() }
}

val networkModules = module {
    single{ NetworkChecker(androidContext()) }
    single{
        OkHttpClient.Builder().apply {
            connectTimeout(CONNECTION_TIMEOUT_LIMIT, TimeUnit.SECONDS)
            callTimeout(CALL_TIMEOUT_LIMIT, TimeUnit.SECONDS)
            readTimeout(READ_TIMEOUT_LIMIT, TimeUnit.SECONDS)
        }.build()
    }
    single<Retrofit> {
        val okHttpClient: OkHttpClient = get()
        Retrofit.Builder().apply {
            baseUrl(BASE_URL)
            client(okHttpClient)
            addConverterFactory(GsonConverterFactory.create())
        }.build()
    }
    single {
        val retrofit: Retrofit = get()
        val apiClient: ApiClient = retrofit.create(ApiClient::class.java)
        AppRepository(apiClient)
    }
}

