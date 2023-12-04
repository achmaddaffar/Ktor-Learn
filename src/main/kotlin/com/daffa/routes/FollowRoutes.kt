package com.daffa.routes

import com.daffa.data.models.Activity
import com.daffa.data.requests.FollowUpdateRequest
import com.daffa.data.responses.BasicApiResponse
import com.daffa.data.util.ActivityType
import com.daffa.service.ActivityService
import com.daffa.service.FollowService
import com.daffa.util.ApiResponseMessages
import com.daffa.util.Constants.Empty
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.followUser(
    followService: FollowService,
    activityService: ActivityService
) {
    authenticate {
        post("/api/following/follow") {
            val request = runCatching<FollowUpdateRequest?> { call.receiveNullable<FollowUpdateRequest>() }.getOrNull()
                ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

            val didUserExist = followService.followUserIfExist(request, call.userId)
            if (didUserExist)
                activityService.createActivity(
                    Activity(
                        timestamp = System.currentTimeMillis(),
                        byUserId = call.userId,
                        toUserId = request.followedUserId,
                        type = ActivityType.FollowedUser.type,
                        parentId = String.Empty
                    )
                ).also {
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse<Unit>(
                            successful = true
                        )
                    )
                }
            else
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse<Unit>(
                        successful = false,
                        message = ApiResponseMessages.USER_NOT_FOUND
                    )
                )
        }
    }
}

fun Route.unfollowUser(
    followService: FollowService
) {
    authenticate {
        delete("/api/following/unfollow") {
            val request = runCatching<FollowUpdateRequest?> { call.receiveNullable<FollowUpdateRequest>() }.getOrNull()
                ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }

            val didUserExist = followService.unfollowUserIfExist(request, call.userId)

            if (didUserExist)
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse<Unit>(
                        successful = true
                    )
                )
            else
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse<Unit>(
                        successful = false,
                        message = ApiResponseMessages.USER_NOT_FOUND
                    )
                )
        }
    }
}