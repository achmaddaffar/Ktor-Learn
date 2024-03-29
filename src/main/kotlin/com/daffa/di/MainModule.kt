package com.daffa.di

import com.daffa.data.repository.activity.ActivityRepository
import com.daffa.data.repository.activity.ActivityRepositoryImpl
import com.daffa.data.repository.comment.CommentRepository
import com.daffa.data.repository.comment.CommentRepositoryImpl
import com.daffa.data.repository.follow.FollowRepository
import com.daffa.data.repository.follow.FollowRepositoryImpl
import com.daffa.data.repository.likes.LikeRepository
import com.daffa.data.repository.likes.LikeRepositoryImpl
import com.daffa.data.repository.post.PostRepository
import com.daffa.data.repository.post.PostRepositoryImpl
import com.daffa.data.repository.user.UserRepository
import com.daffa.data.repository.user.UserRepositoryImpl
import com.daffa.service.*
import com.daffa.util.Constants
import com.google.gson.Gson
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val mainModule = module {

    single {
        val client = KMongo.createClient().coroutine
        client.getDatabase(Constants.DATABASE_NAME)
    }

    single<UserRepository> { UserRepositoryImpl(get()) }
    single { UserService(get(), get()) }

    single<FollowRepository> { FollowRepositoryImpl(get()) }
    single { FollowService(get()) }

    single<PostRepository> { PostRepositoryImpl(get()) }
    single { PostService(get()) }

    single<LikeRepository> { LikeRepositoryImpl(get()) }
    single { LikeService(get(), get(), get()) }

    single<CommentRepository> { CommentRepositoryImpl(get()) }
    single { CommentService(get()) }

    single<ActivityRepository> { ActivityRepositoryImpl(get()) }
    single { ActivityService(get(), get(), get()) }

    single { Gson() }
}