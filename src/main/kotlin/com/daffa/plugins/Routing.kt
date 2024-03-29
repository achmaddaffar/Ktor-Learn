package com.daffa.plugins

import com.daffa.routes.*
import com.daffa.service.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.io.File

fun Application.configureRouting() {
    val userService: UserService by inject()
    val followService: FollowService by inject()
    val postService: PostService by inject()
    val likeService: LikeService by inject()
    val commentService: CommentService by inject()
    val activityService: ActivityService by inject()

    val jwtIssuer = environment.config.property("jwt.domain").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()

    routing {
        // User routes
        authenticate()
        createUser(userService)
        loginUser(
            userService = userService,
            jwtIssuer = jwtIssuer,
            jwtAudience = jwtAudience,
            jwtSecret = jwtSecret
        )
        searchUser(userService)
        getUserProfile(userService)
        getPostForProfile(postService)
        updateUserProfile(userService)

        // Following routes
        followUser(followService, activityService)
        unfollowUser(followService)

        // Post routes
        createPost(postService)
        getPostForFollows(postService)
        deletePost(
            postService,
            likeService,
            commentService
        )

        // Like routes
        likeParent(likeService, activityService)
        unlikeParent(likeService)
        getLikesForParent(likeService)

        // Comment routes
        createComment(commentService, activityService)
        deleteComment(commentService, likeService)
        getCommentForPost(commentService)

        // Activity routes
        getActivities(activityService)

        // static resources
//        static {
//            resources("static")
//        }
        staticResources("", "static")
    }
}
