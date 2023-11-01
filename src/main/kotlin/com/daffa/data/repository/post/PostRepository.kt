package com.daffa.data.repository.post

import com.daffa.data.models.Post
import com.daffa.util.Constants

interface PostRepository {

    suspend fun createPostIfUserExists(post: Post) : Boolean

    suspend fun deletePost(postId: String) : Boolean

    suspend fun getPostByFollows(
        userId: String,
        page: Int = 0,
        pageSize: Int = Constants.DEFAULT_POST_PAGE_SIZE
    ): List<Post>

    suspend fun getPost(postId: String): Post?

    suspend fun getPostForProfile(
        userId: String,
        page: Int = 0,
        pageSize: Int = Constants.DEFAULT_POST_PAGE_SIZE
    ): List<Post>
}