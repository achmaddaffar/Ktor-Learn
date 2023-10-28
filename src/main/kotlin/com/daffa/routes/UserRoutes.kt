package com.daffa.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.daffa.data.requests.CreateAccountRequest
import com.daffa.data.requests.LoginRequest
import com.daffa.data.responses.AuthResponse
import com.daffa.data.responses.BasicApiResponse
import com.daffa.data.responses.UserResponseItem
import com.daffa.service.UserService
import com.daffa.util.ApiResponseMessages
import com.daffa.util.ApiResponseMessages.FIELDS_BLANK
import com.daffa.util.ApiResponseMessages.USER_ALREADY_EXISTS
import com.daffa.util.QueryParams
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.createUser(userService: UserService) {

    route("/api/user/create") {
        post {
            val request = runCatching<CreateAccountRequest?> { call.receiveNullable<CreateAccountRequest>() }.getOrNull() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            if (userService.doesUserWithEmailExist(request.email)) {
                call.respond(
                    BasicApiResponse(
                        successful = false,
                        message = USER_ALREADY_EXISTS
                    )
                )
                return@post
            }

            when(userService.validateCreateAccountRequest(request)) {
                is UserService.ValidationEvent.ErrorFieldEmpty -> {
                    call.respond(
                        BasicApiResponse(
                            successful = false,
                            message = FIELDS_BLANK
                        )
                    )
                    return@post
                }
                is UserService.ValidationEvent.Success -> {
                    userService.createUser(request)
                    call.respond(
                        BasicApiResponse(successful = true)
                    )
                }
            }

            call.respond(
                BasicApiResponse(successful = true)
            )
        }
    }
}


fun Route.loginUser(
    userService: UserService,
    jwtIssuer: String,
    jwtAudience: String,
    jwtSecret: String
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

        val user = userService.getUserByEmail(request.email) ?: run {
            call.respond(
                HttpStatusCode.OK,
                BasicApiResponse(
                    successful = false,
                    message = ApiResponseMessages.INVALID_CREDENTIAL
                )
            )
            return@post
        }

        val isCorrectPassword = userService.isValidPassword(
            enteredPassword = request.password,
            actualPassword = user.password
        )

        if(isCorrectPassword) {
            val expiresIn = 1000L * 60L * 60L * 24L * 365L
            val token = JWT.create()
                .withClaim("userId", user.id)
                .withIssuer(jwtIssuer)
                .withExpiresAt(Date(System.currentTimeMillis() + expiresIn))
                .withAudience(jwtAudience)
                .sign(Algorithm.HMAC256(jwtSecret))
            call.respond(
                HttpStatusCode.OK,
                AuthResponse(
                    token = token
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

fun Route.searchUser(
    userService: UserService
) {
    authenticate {
        get("/api/user/search") {
            val query = call.parameters[QueryParams.PARAM_QUERY]

            if (query.isNullOrBlank()) {
                call.respond(
                    HttpStatusCode.OK,
                    listOf<UserResponseItem>()
                )
                return@get
            }

            val searchResults = userService.searchForUsers(query, call.userId)
            call.respond(
                HttpStatusCode.OK,
                searchResults
            )
        }
    }
}