package com.daffa.plugins

import com.daffa.data.repository.follow.FollowRepository
import com.daffa.data.repository.post.PostRepository
import com.daffa.data.repository.user.UserRepository
import com.daffa.routes.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val userRepository: UserRepository by inject()
    val followRepository: FollowRepository by inject()
    val postRepository: PostRepository by inject()

    routing {
        // User routes
        createUserRoutes(userRepository)
        loginUser(userRepository)

        // Following routes
        followUser(followRepository)
        unfollowUser(followRepository)

        // Post routes
        createPostRoute(postRepository)
    }
}
