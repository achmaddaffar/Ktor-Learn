package com.daffa.util

object Constants {

    const val DATABASE_NAME = "SC_DATABASE"
    const val DEFAULT_POST_PAGE_SIZE = 15
    const val MAX_COMMENT_LENGTH = 2000

    inline val String.Companion.Empty
        get() = ""
}