package com.harry.example.conduitclone.viewmodels


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harry.example.conduitclone.pojos.Article
import com.harry.example.conduitclone.pojos.PostArticle
import com.harry.example.conduitclone.repository.AppRepository
import com.harry.example.conduitclone.utility.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.lang.Exception

class PostArticleViewModel : ViewModel(), KoinComponent {
    private val article: MutableLiveData<Resource<Article?>> = MutableLiveData()
    private val appRepository: AppRepository by inject()
    val currentUserArticle: LiveData<Resource<Article?>>
        get() = article

    fun postOrUpdateArticle(
        token: String?,
        articleTitle: String,
        articleDescription: String,
        articleBody: String,
        tags: List<String>?,
        toBeUpdated: Boolean = false,
        slug: String? = "",
        previousArticle: Article? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            previousArticle?.let {
                if (it.title == articleTitle && it.description == articleDescription && it.articleContent == articleBody && isTagsListEqual(
                        tags,
                        it.tagList
                    )
                ) {
                    setErrorMessage(NO_CHANGE)
                    return@launch
                }
            }
            val message = DataValidation.validateArticleProperties(
                articleTitle,
                articleDescription,
                articleBody,
                slug,
                toBeUpdated
            )
            if (message != VALID) {
                setErrorMessage(message)
            } else {
                val finalAuthToken = if (!token.isNullOrEmpty() && !token.isNullOrBlank()) {
                    TOKEN.plus(" ").plus(token)
                } else {
                    ""
                }
                val article = Article(articleTitle, articleDescription, articleBody, tags)
                val postArticle = PostArticle(article)
                if (toBeUpdated) {
                    updateArticle(finalAuthToken, slug, postArticle)
                } else {
                    postArticle(finalAuthToken, postArticle)
                }
            }
        }
    }

    private suspend fun postArticle(
        token: String,
        postArticle: PostArticle
    ) {
        try {
            val response = appRepository.postArticle(token, postArticle)
            response?.let {
                setArticle(it)
            }
        } catch (exception: Exception) {
            val errorMessage = setMessageWithRespectToException(exception)
            setErrorMessage(errorMessage)
        }
    }

    private suspend fun updateArticle(
        token: String,
        slug: String?,
        postArticle: PostArticle
    ) {
        slug?.let {
            try {
                val response = appRepository.updateArticle(token, it, postArticle)
                response?.let {
                    setArticle(it)
                }
            } catch (exception: Exception) {
                val errorMessage = setMessageWithRespectToException(exception)
                setErrorMessage(errorMessage)
            }
        } ?: run {
            setErrorMessage(SOMETHING_WENT_WRONG)
        }
    }

    private fun isTagsListEqual(previous: List<String>?, current: List<String>?): Boolean {
        previous?.let {
            if (it.size == current?.size)
                return true
            if (it.toTypedArray() contentEquals current?.toTypedArray())
                return true
        }
        return false
    }

    private fun setErrorMessage(message: String) {
        article.postValue(Resource.error(message))
    }

    private fun setArticle(newArticle: Article) {
        article.postValue(Resource.success(newArticle))
    }
}