package com.daffa.plugins

import com.daffa.data.repository.user.UserRepository
import com.daffa.routes.createUserRoutes
import com.daffa.routes.loginUser
import io.ktor.server.routing.*
import io.ktor.server.application.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val userRepository: UserRepository by inject()

    routing {
        createUserRoutes(userRepository)
        loginUser(userRepository)
    }
}
