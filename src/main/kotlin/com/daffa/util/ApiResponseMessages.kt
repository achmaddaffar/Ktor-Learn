package com.daffa.util

object ApiResponseMessages {

    const val USER_ALREADY_EXISTS = "A user with this email already exists."
    const val USER_NOT_FOUND = "The user couldn't be found"
    const val INVALID_CREDENTIAL = "Oops, your username or password is not correct. Please try again."
    const val FIELDS_BLANK = "The fields may not be empty."
    const val COMMENT_TOO_LONG = "The comment length must not exceed ${Constants.MAX_COMMENT_LENGTH} characters"
}