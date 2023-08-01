package com.daffa.plugins

import com.daffa.data.repository.follow.FollowRepository
import com.daffa.data.repository.post.PostRepository
import com.daffa.data.repository.user.UserRepository
import com.daffa.routes.*
import com.daffa.service.FollowService
import com.daffa.service.PostService
import com.daffa.service.UserService
import io.ktor.server.routing.*
import io.ktor.server.application.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val userRepository: UserRepository by inject()
    val userService: UserService by inject()

    val followRepository: FollowRepository by inject()
    val followService: FollowService by inject()

    val postRepository: PostRepository by inject()
    val postService: PostService by inject()

    routing {
        // User routes
        createUserRoutes(userService)
        loginUser(userService)

        // Following routes
        followUser(followService)
        unfollowUser(followService)

        // Post routes
        createPostRoute(postService)
    }
}
