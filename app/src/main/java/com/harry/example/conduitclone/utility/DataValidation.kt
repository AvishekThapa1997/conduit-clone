package com.harry.example.conduitclone.utility

import android.util.Patterns

object DataValidation {
    fun validateUserCredentials(
        user_email: String = "",
        user_name: String = "",
        user_password: String = "",
        match_password: String = "",
        for_registration: Boolean = true
    ): String {
        val email = user_email.trim()
        val username = user_name.trim()
        val password = user_password.trim()
        val confirmPassword = match_password.trim()
        if (email.isEmptyOrIsBlank()) {
            return EMAIL_CANNOT_BE_EMPTY
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return INVALID_MAIL
        }
        if (for_registration) {
            if (username.isEmptyOrIsBlank()) {
                return USERNAME_CANNOT_BE_EMPTY
            }
        }
        if (password.isEmpty() || password.isBlank()) {
            return PASSWORD_CANNOT_BE_EMPTY
        }
        if (for_registration) {
            if (password.length < 8) {
                return PASSWORD_MINIMUM_CHARACTERS
            }
            if (confirmPassword.isEmptyOrIsBlank()) {
                return CONFIRM_PASSWORD_CANNOT_BE_EMPTY
            }
            if (password != confirmPassword) {
                return SHOULD_BE_SAME
            }
        }
        return VALID
    }

    fun validateArticleProperties(
        articleTitle: String,
        articleDescription: String,
        articleBody: String,
        slug: String?,
        toBeUpdated: Boolean = false
    ): String {
        if (articleTitle.isEmptyOrIsBlank()) {
            return TITLE_CANNOT_BE_EMPTY
        }
        if (articleDescription.isEmptyOrIsBlank()) {
            return DESCRIPTION_CANNOT_BE_EMPTY
        }
        if (articleBody.isEmptyOrIsBlank()) {
            return BODY_CANNOT_BE_EMPTY
        }
        if (toBeUpdated) {
            slug?.let {
                if (it.isEmptyOrIsBlank())
                    return SOMETHING_WENT_WRONG
            }
        }
        return VALID
    }
}