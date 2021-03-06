package com.harry.example.conduitclone.viewmodels


import androidx.lifecycle.*
import com.harry.example.conduitclone.pojos.*

import com.harry.example.conduitclone.repository.AppRepository
import com.harry.example.conduitclone.utility.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject


class HomeViewModel : ViewModel(), KoinComponent {

    val offset
        get() = appRepository.offset
    private val appRepository: AppRepository by inject()
    private val _apiResponse: MutableLiveData<Resource<List<Article>?>> =
        MutableLiveData()

    val apiResponse: LiveData<Resource<List<Article>?>>
        get() = _apiResponse

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    fun getArticles(token: String, username: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            val finalToken: String = if (token.isNotEmpty() || token.isNotBlank()) {
                TOKEN.plus(" ").plus(token)
            } else {
                token
            }
            setResponseFromNetwork(finalToken, username)
        }
    }

    fun favouriteArticle(token: String?, slug: String?, position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val article = appRepository.favouriteArticle(TOKEN.plus(" ").plus(token), slug)
                article?.let {
                    it.article?.let { updatedArticle ->
                        updateList(position, updatedArticle)
                    }
                }
            } catch (exception: Exception) {
                _errorMessage.postValue(setMessageWithRespectToException(exception))
            }
        }
    }

    fun unFavouriteArticle(
        token: String?,
        slug: String?,
        position: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val article = appRepository.unFavouriteArticle(TOKEN.plus(" ").plus(token), slug)
                article?.let {
                    it.article?.let {
                        updateList(position, it)
                    }
                }
            } catch (exception: Exception) {
                _errorMessage.postValue(setMessageWithRespectToException(exception))
            }
        }
    }

    private fun updateList(position: Int, article: Article) {
        val updatedArticleList = mutableListOf<Article>()
        _apiResponse.value?.data?.let {
            updatedArticleList.addAll(it)
        }
        updatedArticleList[position] = article
        setListOfArticles(updatedArticleList)
    }

    private suspend fun getResponseFromNetwork(token: String): List<Article>? {
        try {
            val response = appRepository.getAllArticles(token)
            response?.articles?.apply {
                if (isNotEmpty()) {
                    return this
                }
            }
        } catch (exception: Exception) {
            val message = setMessageWithRespectToException(exception)
            setErrorMessage(message)
        }
        return null
    }

    private suspend fun setResponseFromNetwork(token: String, username: String) {
        val listOfArticles = getResponseFromNetwork(token)
        listOfArticles?.apply {
            if (isNotEmpty()) {
                val filteredList = this.filter {
                    !it.author?.username.equals(username)
                }
                _apiResponse.value?.data?.let {
                    if (it.isNotEmpty()) {
                        val newList = mutableListOf<Article>()
                        newList.addAll(it)
                        newList.addAll(filteredList)
                        setListOfArticles(newList)
                    }
                } ?: run {
                    setListOfArticles(filteredList)
                }
            }
        }
    }

    private fun setListOfArticles(articles: List<Article>) {
        _apiResponse.postValue(
            Resource.success(
                articles
            )
        )
    }

    private fun setErrorMessage(message: String) {
        _apiResponse.postValue(
            Resource.error(
                message
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        appRepository.offset = 0
    }

    fun setMemoryCacheArticleList(articles: List<Article>) {
        viewModelScope.launch {
            setListOfArticles(articles)
        }
    }
}