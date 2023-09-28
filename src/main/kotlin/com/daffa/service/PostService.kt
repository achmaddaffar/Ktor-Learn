package com.daffa.service

import com.daffa.data.models.Post
import com.daffa.data.repository.post.PostRepository
import com.daffa.data.requests.CreatePostRequest
import com.daffa.util.Constants

class PostService(
    private val repository: PostRepository
) {

    suspend fun createPostIfUserExist(request: CreatePostRequest, userId: String): Boolean {
        return repository.createPostIfUserExists(
            Post(
                imageUrl = "",
                userId = userId,
                timestamp = System.currentTimeMillis(),
                description = request.description
            )
        )
    }

    suspend fun getPostForFollows(
        userId: String,
        page: Int,
        pageSize: Int = Constants.DEFAULT_POST_PAGE_SIZE
    ): List<Post> {
        return repository.getPostByFollows(
            userId,
            page,
            pageSize
        )
    }

    suspend fun getPost(postId: String): Post? = repository.getPost(postId)

    suspend fun deletePost(postId: String) {
        repository.deletePost(postId)
    }
}