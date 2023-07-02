package com.daffa.routes

import com.daffa.data.models.Post
import com.daffa.data.repository.post.PostRepository
import com.daffa.data.requests.CreatePostRequest
import com.daffa.data.responses.BasicApiResponse
import com.daffa.service.PostService
import com.daffa.util.ApiResponseMessages
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.createPostRoute(
    postService: PostService
) {
    post("/api/post/create") {
        val request = kotlin.runCatching { call.receiveNullable<CreatePostRequest>() }.getOrNull() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val didUserExist = postService.createPostIfUserExist(request)

        if (didUserExist) {
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
                    message = ApiResponseMessages.USER_NOT_FOUND
                )
            )
        }
    }
}