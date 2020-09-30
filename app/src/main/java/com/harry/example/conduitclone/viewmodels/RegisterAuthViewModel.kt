package com.harry.example.conduitclone.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.harry.example.conduitclone.pojos.CredentialError
import com.harry.example.conduitclone.pojos.RegisterAuth
import com.harry.example.conduitclone.pojos.User
import com.harry.example.conduitclone.utility.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterAuthViewModel : BaseAuthViewModel() {
    val registeredUserResponse: LiveData<Resource<User>>
        get() = userResponse

    fun registerUser(email: String, username: String, password: String, confirm_password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val message =
                DataValidation.validateUserCredentials(email, username, password, confirm_password)
            if (message != VALID) {
                setErrorMessage(message)
            } else {
                val newUser = RegisterAuth(User(email, username, password))
                    try {
                        val response = appRepository.registerUser(newUser)
                        response?.let {
                            it.credentialError?.let {
                                val errorMessage = credentialsErrorMessage(it)
                                if (!errorMessage.isEmptyOrIsBlank())
                                    setErrorMessage(errorMessage)
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

    private fun credentialsErrorMessage(credentialError: CredentialError): String {
        credentialError.apply {
            emailErrors?.apply {
                return "Email ".plus(get(0))
            }
            passwordErrors?.apply {
                return "Password ".plus(get(0))
            }
            usernameErrors?.apply {
                return "Username ".plus(get(0))
            }
        }
        return ""
    }
}