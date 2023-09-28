package com.daffa.routes

import com.daffa.data.requests.CreatePostRequest
import com.daffa.data.requests.DeletePostRequest
import com.daffa.data.responses.BasicApiResponse
import com.daffa.service.LikeService
import com.daffa.service.PostService
import com.daffa.service.UserService
import com.daffa.util.ApiResponseMessages
import com.daffa.util.Constants
import com.daffa.util.Constants.Empty
import com.daffa.util.QueryParams
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.createPost(
    postService: PostService,
    userService: UserService
) {
    authenticate {
        post("/api/post/create") {
            val request = kotlin.runCatching { call.receiveNullable<CreatePostRequest>() }.getOrNull() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            ifEmailBelongsToUser(
                userId = request.userId,
                validateEmail = userService::doesEmailBelongToUserId
            ) {
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
    }
}

fun Route.getPostForFollows(
    postService: PostService,
    userService: UserService
) {
    authenticate {
        get("/api/post/get") {
            val userId = call.parameters[QueryParams.PARAM_USER_ID] ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
            val pageSize =
                call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_POST_PAGE_SIZE

            ifEmailBelongsToUser(
                userId = userId,
                validateEmail = userService::doesEmailBelongToUserId,
            ) {
                val posts = postService.getPostForFollows(
                    userId = userId,
                    page = page,
                    pageSize = pageSize
                )
                call.respond(
                    HttpStatusCode.OK,
                    posts
                )
            }
        }
    }
}

fun Route.deletePost(
    postService: PostService,
    userService: UserService,
    likeService: LikeService
) {
    authenticate {
        delete("/api/post/delete") {
            val request = kotlin.runCatching { call.receiveNullable<DeletePostRequest>() }.getOrNull() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            val post = postService.getPost(request.postId)
            if (post == null)
                call.respond(
                    HttpStatusCode.NotFound
                ).also { return@delete }
            ifEmailBelongsToUser(
                userId = post?.userId ?: String.Empty,
                validateEmail = userService::doesEmailBelongToUserId
            ) {
                postService.deletePost(request.postId)
                likeService.deleteLikesForParent(request.postId)

                // TODO: Delete comments from post
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}