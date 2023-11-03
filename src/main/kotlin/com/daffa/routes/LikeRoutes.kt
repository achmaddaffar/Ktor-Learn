package com.daffa.routes

import com.daffa.data.requests.LikeUpdateRequest
import com.daffa.data.responses.BasicApiResponse
import com.daffa.data.util.ParentType
import com.daffa.service.ActivityService
import com.daffa.service.LikeService
import com.daffa.util.ApiResponseMessages
import com.daffa.util.QueryParams
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.likeParent(
    likeService: LikeService,
    activityService: ActivityService
) {
    authenticate {
        post("/api/like") {
            val request = kotlin.runCatching { call.receiveNullable<LikeUpdateRequest>() }.getOrNull() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val likeSuccessful = likeService.likeParent(
                call.userId,
                request.parentId,
                request.parentType
            )
            val userId = call.userId
            if (likeSuccessful) {
                activityService.addLikeActivity(
                    byUserId = userId,
                    parentType = ParentType.fromType(request.parentType),
                    parentId = request.parentId
                )
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse(
                        successful = true
                    )
                )
            }
            else
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

fun Route.unlikeParent(
    likeService: LikeService,
) {
    authenticate {
        delete("/api/unlike") {
            val request = kotlin.runCatching { call.receiveNullable<LikeUpdateRequest>() }.getOrNull() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            val unlikeSuccessful = likeService.unlikeParent(
                call.userId,
                request.parentId
            )
            if (unlikeSuccessful)
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse(
                        successful = true
                    )
                )
            else
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

fun Route.getLikesForParent(likeService: LikeService) {
    authenticate {
        get("/api/like/parent") {
            val parentId = call.parameters[QueryParams.PARAM_PARENT_ID] ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val usersWhoLikedParent = likeService.getUsersWhoLikedParent(
                parentId = parentId,
                userId = call.userId
            )
            call.respond(
                HttpStatusCode.OK,
                usersWhoLikedParent
            )
        }
    }
}