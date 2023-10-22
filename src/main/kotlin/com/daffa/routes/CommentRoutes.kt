package com.daffa.routes

import com.daffa.data.requests.CreateCommentRequest
import com.daffa.data.requests.DeleteCommentRequest
import com.daffa.data.responses.BasicApiResponse
import com.daffa.service.ActivityService
import com.daffa.service.CommentService
import com.daffa.service.LikeService
import com.daffa.service.UserService
import com.daffa.util.ApiResponseMessages
import com.daffa.util.QueryParams
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.createComment(
    commentService: CommentService,
    activityService: ActivityService
) {
    authenticate {
        post("/api/comment/create") {
            val request = runCatching { call.receiveNullable<CreateCommentRequest>() }.getOrNull() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val userId = call.userId
            when(commentService.createComment(request, call.userId)) {
                is CommentService.ValidationEvent.ErrorFieldEmpty -> {
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse(
                            successful = false,
                            message = ApiResponseMessages.FIELDS_BLANK
                        )
                    )
                }
                is CommentService.ValidationEvent.ErrorCommentTooLong -> {
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse(
                            successful = false,
                            message = ApiResponseMessages.COMMENT_TOO_LONG
                        )
                    )
                }
                is CommentService.ValidationEvent.Success -> {
                    activityService.addCommentActivity(
                        byUserId = userId,
                        postId = request.postId
                    )
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse(
                            successful = true
                        )
                    )
                }
            }
        }
    }
}

fun Route.getCommentForPost(
    commentService: CommentService
) {
    authenticate {
        get("/api/comment/get") {
            val postId = call.parameters[QueryParams.PARAM_POST_ID] ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val comments = commentService.getCommentsForPost(postId)
            call.respond(HttpStatusCode.OK, comments)
        }
    }
}

fun Route.deleteComment(
    commentService: CommentService,
    likeService: LikeService
) {
    authenticate {
        delete("/api/comment/delete") {
            val request = runCatching { call.receiveNullable<DeleteCommentRequest>() }.getOrNull() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            val comment = commentService.getCommentById(request.commentId)
            if (comment?.userId != call.userId)
                call.respond(HttpStatusCode.Unauthorized).also { return@delete }

            val deleted = commentService.deleteComment(request.commentId)
            if (deleted) {
                likeService.deleteLikesForParent(request.commentId)
                call.respond(HttpStatusCode.OK, BasicApiResponse(successful = true))
            } else {
                call.respond(HttpStatusCode.NotFound, BasicApiResponse(successful = false))
            }
        }
    }
}