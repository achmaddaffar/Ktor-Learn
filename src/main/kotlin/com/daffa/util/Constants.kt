package com.daffa.util

object Constants {

    const val DATABASE_NAME = "SC_DATABASE"
    const val DEFAULT_POST_PAGE_SIZE = 15
    const val DEFAULT_ACTIVITY_PAGE_SIZE = 15
    const val MAX_COMMENT_LENGTH = 2000

    const val BASE_URL = "http://localhost:8004/"
    const val PROFILE_PICTURE_PATH = "build/resources/main/static/profile_pictures/"

    inline val String.Companion.Empty
        get() = ""
}