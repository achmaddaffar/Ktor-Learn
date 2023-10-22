package com.daffa.service

import com.daffa.data.models.Activity
import com.daffa.data.repository.activity.ActivityRepository
import com.daffa.data.repository.comment.CommentRepository
import com.daffa.data.repository.post.PostRepository
import com.daffa.data.util.ActivityType
import com.daffa.data.util.ParentType

class ActivityService(
    private val activityRepository: ActivityRepository,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository
) {
    suspend fun getActivitiesForUser(
        userId: String,
        page: Int,
        pageSize: Int
    ): List<Activity> {
        return activityRepository.getActivitiesForUser(userId, page, pageSize)
    }

    suspend fun addCommentActivity(
        byUserId: String,
        postId: String
    ): Boolean {
        val userIdOfPost = postRepository.getPost(postId)?.userId ?: return false
        if (byUserId == userIdOfPost)
            return false

        return activityRepository.createActivity(
            Activity(
                timestamp = System.currentTimeMillis(),
                byUserId = byUserId,
                toUserId = userIdOfPost,
                type = ActivityType.CommentedOnPost.type,
                parentId = postId
            )
        )
    }

    suspend fun addLikeActivity(
        byUserId: String,
        parentType: ParentType,
        parentId: String
    ): Boolean {
        val toUserId = when (parentType) {
            is ParentType.Post -> {
                postRepository.getPost(parentId)?.userId
            }

            is ParentType.Comment -> {
                commentRepository.getComment(parentId)?.userId
            }

            is ParentType.None -> return false
        } ?: return false

        if (byUserId == toUserId)
            return false

        return activityRepository.createActivity(
            Activity(
                timestamp = System.currentTimeMillis(),
                byUserId = byUserId,
                toUserId = toUserId,
                type = when (parentType) {
                    is ParentType.Post -> ActivityType.LikedPost.type
                    is ParentType.Comment -> ActivityType.LikedComment.type
                    else -> ActivityType.LikedPost.type
                },
                parentId = parentId
            )
        )
    }

    suspend fun createActivity(activity: Activity): Boolean {
        return activityRepository.createActivity(activity)
    }

    suspend fun deleteActivity(activityId: String): Boolean {
        return activityRepository.deleteActivity(activityId)
    }
}