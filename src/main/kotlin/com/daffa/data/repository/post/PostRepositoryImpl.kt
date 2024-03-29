package com.daffa.data.repository.post

import com.daffa.data.models.Following
import com.daffa.data.models.Post
import com.daffa.data.models.User
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.`in`

class PostRepositoryImpl(
    db: CoroutineDatabase
) : PostRepository {

    private val posts = db.getCollection<Post>()
    private val following = db.getCollection<Following>()
    private val users = db.getCollection<User>()

    override suspend fun createPost(post: Post): Boolean {
        return posts.insertOne(post).wasAcknowledged()
    }

    override suspend fun deletePost(postId: String): Boolean {
        return posts.deleteOneById(postId).wasAcknowledged()
    }

    override suspend fun getPostByFollows(
        userId: String,
        page: Int,
        pageSize: Int
    ): List<Post> {
        val userIdsFromFollows = following.find(
            Following::followingUserId eq userId
        )
            .toList()
            .map {
                it.followedUserId
            }

        return posts.find(
            Post::userId `in` userIdsFromFollows
        )
            .descendingSort(Post::timestamp)
            .skip(page * pageSize)
            .limit(pageSize)
            .toList()
    }

    override suspend fun getPost(postId: String): Post? {
        return posts.findOneById(postId)
    }

    override suspend fun getPostForProfile(userId: String, page: Int, pageSize: Int): List<Post> {
        return posts.find(
            Post::userId `in` userId
        )
            .descendingSort(Post::timestamp)
            .skip(page * pageSize)
            .limit(pageSize)
            .toList()
    }
}