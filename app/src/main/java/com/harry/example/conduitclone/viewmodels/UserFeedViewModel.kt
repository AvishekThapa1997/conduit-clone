package com.harry.example.conduitclone.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harry.example.conduitclone.pojos.Article
import com.harry.example.conduitclone.repository.AppRepository
import com.harry.example.conduitclone.utility.Resource
import com.harry.example.conduitclone.utility.TOKEN
import com.harry.example.conduitclone.utility.setMessageWithRespectToException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*


class UserFeedViewModel : ViewModel(), KoinComponent {
    private val appRepository: AppRepository by inject()
    private val userFeed: MutableLiveData<Resource<List<Article>?>> = MutableLiveData()
    val currentUserFeed: LiveData<Resource<List<Article>?>>
        get() = userFeed
    private var offset = 0
    fun getUserFeed(token: String?, username: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val finalToken = if (!token.isNullOrBlank() && !token.isNullOrEmpty()) {
                TOKEN.plus(" ").plus(token)
            } else {
                ""
            }
            username?.apply {
                try {
                    val response = appRepository.getAllArticles(finalToken, this)
                    response?.let {
                        it.articles?.let {
                            setUserFeed(it)
                        }
                    }
                } catch (exception: Exception) {
                    setErrorMessage(setMessageWithRespectToException(exception))
                }
            }
        }
    }

    private fun setUserFeed(articles: List<Article>) {
        userFeed.postValue(Resource.success(articles))
    }

    private fun setErrorMessage(message: String) {
        userFeed.postValue(Resource.error(message))
    }

    fun deleteArticle(token: String?, position: Int, slug: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val finalToken = if (!token.isNullOrBlank() && !token.isNullOrEmpty()) {
                TOKEN.plus(" ").plus(token)
            } else {
                ""
            }
            slug?.let {
                try {
                    val isArticleDeletionSuccessful = appRepository.deleteArticle(finalToken, slug)
                    if (isArticleDeletionSuccessful) {
                        val newList = mutableListOf<Article>()
                        userFeed.value?.data?.let {
                            newList.addAll(it)
                            newList.removeAt(position)
                            setUserFeed(newList)
                        }
                    }
                } catch (exception: Exception) {
                    setErrorMessage(setMessageWithRespectToException(exception))
                }
            }
        }
    }
//    fun addNewArticleToList(article: Article) {
//        viewModelScope.launch(Dispatchers.Default) {
//            userFeed.value?.data?.let {
//                    val newList = mutableListOf<Article>()
//                    newList.addAll(it)
//                    newList.add(article)
//                    setUserFeed(newList)
//                }
//        }
//    }
}