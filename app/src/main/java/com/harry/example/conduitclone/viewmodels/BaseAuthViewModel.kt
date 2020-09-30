package com.harry.example.conduitclone.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.harry.example.conduitclone.pojos.User
import com.harry.example.conduitclone.repository.AppRepository
import com.harry.example.conduitclone.utility.Resource
import org.koin.core.KoinComponent
import org.koin.core.inject

open class BaseAuthViewModel : ViewModel(), KoinComponent {
    protected val appRepository: AppRepository by inject()
    private val currentUser: MutableLiveData<Resource<User>> = MutableLiveData()
    protected val userResponse: LiveData<Resource<User>>
        get() = currentUser

    protected fun setCurrentUser(user: User) {
        currentUser.postValue(Resource.success(user))
    }

    protected fun setErrorMessage(message: String) {
        currentUser.postValue(Resource.error(message))
    }
}