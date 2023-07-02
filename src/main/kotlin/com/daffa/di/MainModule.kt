package com.daffa.di

import com.daffa.data.repository.follow.FollowRepository
import com.daffa.data.repository.follow.FollowRepositoryImpl
import com.daffa.data.repository.post.PostRepository
import com.daffa.data.repository.post.PostRepositoryImpl
import com.daffa.data.repository.user.UserRepository
import com.daffa.data.repository.user.UserRepositoryImpl
import com.daffa.service.FollowService
import com.daffa.service.PostService
import com.daffa.service.UserService
import com.daffa.util.Constants
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val mainModule = module {

    single {
        val client = KMongo.createClient().coroutine
        client.getDatabase(Constants.DATABASE_NAME)
    }

    single<UserRepository> {
        UserRepositoryImpl(get())
    }

    single {
        UserService(get())
    }

    single<FollowRepository> {
        FollowRepositoryImpl(get())
    }

    single {
        FollowService(get())
    }

    single<PostRepository> {
        PostRepositoryImpl(get())
    }

    single {
        PostService(get())
    }
}