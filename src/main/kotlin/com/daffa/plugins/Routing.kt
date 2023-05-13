package com.daffa.plugins

import com.daffa.data.repository.follow.FollowRepository
import com.daffa.data.repository.user.UserRepository
import com.daffa.routes.createUserRoutes
import com.daffa.routes.followUser
import com.daffa.routes.loginUser
import com.daffa.routes.unfollowUser
import io.ktor.server.routing.*
import io.ktor.server.application.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val userRepository: UserRepository by inject()
    val followRepository: FollowRepository by inject()

    routing {
        // User routes
        createUserRoutes(userRepository)
        loginUser(userRepository)

        // Following routes
        followUser(followRepository)
        unfollowUser(followRepository)
    }
}
