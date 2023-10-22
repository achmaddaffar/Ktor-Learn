package com.daffa.routes

import com.daffa.data.requests.CreatePostRequest
import com.daffa.data.requests.DeletePostRequest
import com.daffa.data.responses.BasicApiResponse
import com.daffa.service.CommentService
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
) {
    authenticate {
        post("/api/post/create") {
            val request = kotlin.runCatching { call.receiveNullable<CreatePostRequest>() }.getOrNull() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val userId = call.userId

            val didUserExist = postService.createPostIfUserExist(request, userId)

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

fun Route.getPostForFollows(
    postService: PostService,
) {
    authenticate {
        get("/api/post/get") {
            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
            val pageSize =
                call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_POST_PAGE_SIZE

            val posts = postService.getPostForFollows(
                userId = call.userId,
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

fun Route.deletePost(
    postService: PostService,
    likeService: LikeService,
    commentService: CommentService
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

            if (post?.userId == call.userId) {
                postService.deletePost(request.postId)
                likeService.deleteLikesForParent(request.postId)
                commentService.deleteCommentsForPost(request.postId)
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }
}