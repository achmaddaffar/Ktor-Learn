package com.daffa.data.repository.follow

import org.litote.kmongo.coroutine.CoroutineDatabase

class FollowRepositoryImpl(
    db: CoroutineDatabase
): FollowRepository {
    override suspend fun followUser(
        followingUserId: String,
        followedUserId: String
    ): Boolean {
        return true
    }
}