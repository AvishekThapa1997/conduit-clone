package com.harry.example.conduitclone.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.harry.example.conduitclone.pojos.User
import com.harry.example.conduitclone.utility.Resource
import com.harry.example.conduitclone.utility.TOKEN
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SplashViewModel : BaseAuthViewModel() {
    val currentUser: LiveData<Resource<User>>
        get() = userResponse

    fun currentUser(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentUser = appRepository.currentUser(TOKEN.plus(" ").plus(token))
                currentUser?.let {
                    setCurrentUser(it)
                } ?: run {
                    setErrorMessage("")
                }
            } catch (exception: Exception) {
                setErrorMessage("")
            }
        }

    }
}