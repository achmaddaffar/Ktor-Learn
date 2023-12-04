package com.daffa.routes

import com.daffa.data.requests.UpdateProfileRequest
import com.daffa.data.responses.BasicApiResponse
import com.daffa.data.responses.UserResponseItem
import com.daffa.service.PostService
import com.daffa.service.UserService
import com.daffa.util.ApiResponseMessages
import com.daffa.util.Constants
import com.daffa.util.Constants.BASE_URL
import com.daffa.util.Constants.PROFILE_PICTURE_PATH
import com.daffa.util.QueryParams
import com.daffa.util.save
import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.io.File

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

fun Route.getUserProfile(
    userService: UserService
) {
    authenticate {
        get("/api/user/profile") {
            val userId = call.parameters[QueryParams.PARAM_USER_ID]

            if (userId.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val profileResponse = userService.getUserProfile(userId, call.userId) ?: run {
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse<Unit>(
                        successful = false,
                        message = ApiResponseMessages.USER_NOT_FOUND
                    )
                )
                return@get
            }
            call.respond(
                HttpStatusCode.OK,
                profileResponse
            )
        }
    }
}


fun Route.getPostForProfile(
    postService: PostService,
) {
    authenticate {
        get("/api/user/posts") {
            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
            val pageSize =
                call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_POST_PAGE_SIZE

            val posts = postService.getPostForProfile(
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

fun Route.updateUserProfile(
    userService: UserService
) {
    val gson: Gson by inject()
    authenticate {
        put("/api/user/update") {
            val multipart = call.receiveMultipart()
            var updateProfileRequest: UpdateProfileRequest? = null
            var fileName: String? = null
            multipart.forEachPart { partData ->
                when (partData) {
                    is PartData.FormItem -> {
                        if (partData.name == "update_profile_data") {
                            updateProfileRequest = gson.fromJson(
                                partData.value,
                                UpdateProfileRequest::class.java
                            )
                        }
                    }

                    is PartData.FileItem -> {
                        fileName = partData.save(PROFILE_PICTURE_PATH)
                    }

                    else -> Unit
                }
                partData.dispose()
            }

            updateProfileRequest?.let { request ->
                val profilePictureUrl = "${BASE_URL}profile_pictures/$fileName"
                val updateAcknowledged = userService.updateUser(
                    userId = call.userId,
                    profileImageUrl = profilePictureUrl,
                    updateProfileRequest = request
                )
                if (updateAcknowledged) {
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse<Unit>(
                            successful = true
                        )
                    )
                } else {
                    File(profilePictureUrl).delete()
                    call.respond(HttpStatusCode.InternalServerError)
                }
            } ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
        }
    }
}