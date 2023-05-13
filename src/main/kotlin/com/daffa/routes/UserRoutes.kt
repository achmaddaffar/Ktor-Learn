package com.daffa.routes

import com.daffa.data.repository.user.UserRepository
import com.daffa.data.models.User
import com.daffa.data.requests.CreateAccountRequest
import com.daffa.data.requests.LoginRequest
import com.daffa.data.responses.BasicApiResponse
import com.daffa.util.ApiResponseMessages
import com.daffa.util.ApiResponseMessages.FIELDS_BLANK
import com.daffa.util.ApiResponseMessages.USER_ALREADY_EXISTS
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.createUserRoutes(userRepository: UserRepository) {

    route("/api/user/create") {
        post {
            val request = kotlin.runCatching<CreateAccountRequest?> { call.receiveNullable<CreateAccountRequest>() }.getOrNull() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val userExists = userRepository.getUserByEmail(request.email) != null
            if (userExists) {
                call.respond(
                    BasicApiResponse(
                        successful = false,
                        message = USER_ALREADY_EXISTS
                    )
                )
                return@post
            }

            if (request.email.isBlank() || request.password.isBlank() || request.username.isBlank()) {
                call.respond(
                    BasicApiResponse(
                        successful = false,
                        message = FIELDS_BLANK
                    )
                )
                return@post
            }

            userRepository.createUser(
                User(
                    email = request.email,
                    username = request.username,
                    password = request.password,
                    profileImageUrl = "",
                    bio = "bio test",
                    gitHubUrl = null,
                    instagramUrl = null,
                    linkedInUrl = null
                )
            )
            call.respond(
                BasicApiResponse(successful = true)
            )
        }
    }
}


fun Route.loginUser(
    userRepository: UserRepository
) {
    post("/api/user/login") {
        val request =
            kotlin.runCatching { call.receiveNullable<LoginRequest>() }.getOrNull() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

        if (request.email.isBlank() || request.password.isBlank()) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val isCorrectPassword = userRepository.doesPasswordForUserMatch(
            email = request.email,
            enteredPassword = request.password
        )

        if(isCorrectPassword) {
            call.respond(
                HttpStatusCode.OK,
                BasicApiResponse(
                    successful = true
                )
            )
        } else {
            call.respond(
                HttpStatusCode.OK,
                BasicApiResponse(
                    successful = false,
                    message = ApiResponseMessages.INVALID_CREDENTIAL
                )
            )
        }
    }
}