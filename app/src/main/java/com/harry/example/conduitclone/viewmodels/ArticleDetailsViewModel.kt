package com.harry.example.conduitclone.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harry.example.conduitclone.pojos.Author
import com.harry.example.conduitclone.repository.AppRepository
import com.harry.example.conduitclone.utility.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.Exception

class ArticleDetailsViewModel : ViewModel(), KoinComponent {
    private val appRepository: AppRepository by inject()
    private val newAuthorProfile: MutableLiveData<Resource<Author?>> = MutableLiveData()
    val updatedAuthorProfile: LiveData<Resource<Author?>>
        get() = newAuthorProfile

    fun followUser(token: String?, username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if ((!token.isNullOrEmpty() && !token.isNullOrBlank()) && (username.isNotEmpty() && username.isNotBlank())) {
                try {
                    val authtoken = TOKEN.plus(" ").plus(token)
                    val updatedProfile = appRepository.followUser(authtoken, username)
                    updatedProfile?.apply {
                        authorProfile?.let {
                            setUpdatedProfile(authorProfile)
                            return@launch
                        }
                    }
                    setErrorMessage(SOMETHING_WENT_WRONG)
                } catch (exception: Exception) {
                    setErrorMessage(setMessageWithRespectToException(exception))
                }
                return@launch
            }
            setErrorMessage(NOT_LOGGED_IN)
        }
    }

    fun unfollowUser(token: String?, username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if ((!token.isNullOrEmpty() && !token.isNullOrBlank()) && (username.isNotEmpty() && username.isNotBlank())) {
                try {
                    val authToken = TOKEN.plus(" ").plus(token)
                    val updatedProfile = appRepository.unFollowUser(authToken, username)
                    updatedProfile?.apply {
                        authorProfile?.let {
                            setUpdatedProfile(authorProfile)
                            return@launch
                        }
                    }
                    setErrorMessage(SOMETHING_WENT_WRONG)
                } catch (exception: Exception) {
                    setErrorMessage(setMessageWithRespectToException(exception))
                }
                return@launch
            }
            setErrorMessage(NOT_LOGGED_IN)
        }
    }

    private fun setUpdatedProfile(author: Author) {
        newAuthorProfile.postValue(Resource.success(author))
    }

    private fun setErrorMessage(message: String) {
        newAuthorProfile.postValue(Resource.error(message))
    }
}