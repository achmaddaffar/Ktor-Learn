package com.daffa.service

import com.daffa.data.models.Comment
import com.daffa.data.repository.comment.CommentRepository
import com.daffa.data.requests.CreateCommentRequest
import com.daffa.util.Constants

class CommentService(
    private val repository: CommentRepository
) {
    suspend fun createComment(createCommentRequest: CreateCommentRequest, userId: String): ValidationEvent {
        createCommentRequest.apply {
            if (comment.isBlank() || postId.isBlank())
                return ValidationEvent.ErrorFieldEmpty
            if (comment.length > Constants.MAX_COMMENT_LENGTH)
                return ValidationEvent.ErrorCommentTooLong
        }
        repository.createComment(
            Comment(
                comment = createCommentRequest.comment,
                userId = userId,
                postId = createCommentRequest.postId,
                timestamp = System.currentTimeMillis()
            )
        )
        return ValidationEvent.Success
    }

    suspend fun deleteCommentsForPost(postId: String): Boolean {
        return repository.deleteCommentsFromPost(postId)
    }

    suspend fun deleteComment(commentId: String): Boolean {
        return repository.deleteComment(commentId)
    }

    suspend fun getCommentsForPost(postId: String): List<Comment> {
        return repository.getCommentsForPost(postId)
    }

    suspend fun getCommentById(commentId: String): Comment? {
        return repository.getComment(commentId)
    }

    sealed class ValidationEvent {
        object ErrorFieldEmpty : ValidationEvent()
        object ErrorCommentTooLong : ValidationEvent()
        object Success : ValidationEvent()
    }
}