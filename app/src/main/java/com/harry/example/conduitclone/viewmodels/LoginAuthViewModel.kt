package com.harry.example.conduitclone.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.harry.example.conduitclone.pojos.LoginAuth
import com.harry.example.conduitclone.pojos.User
import com.harry.example.conduitclone.utility.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginAuthViewModel : BaseAuthViewModel() {
    val currentUserResponse: LiveData<Resource<User>>
        get() = userResponse

    fun loginUser(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val message = DataValidation.validateUserCredentials(
                email,
                user_password = password,
                for_registration = false
            )
            if (message != VALID) {
                setErrorMessage(message)
            } else {
                val user = LoginAuth(User(email, password))
                try {
                    val res = appRepository.loginUser(user)
                    res?.let {
                        it.credentialError?.loginError?.let {
                            setErrorMessage("Email or Password ".plus(it.get(0)))
                        } ?: run {
                            setCurrentUser(it.user)
                        }
                    } ?: run {
                        setErrorMessage(SOMETHING_WENT_WRONG)
                    }
                } catch (exception: Exception) {
                    setErrorMessage(setMessageWithRespectToException(exception))
                }
            }
        }
    }
}