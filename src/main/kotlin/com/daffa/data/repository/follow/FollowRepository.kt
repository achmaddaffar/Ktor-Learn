package com.daffa.data.repository.follow

interface FollowRepository {

    suspend fun followUser(
        followingUserId: String,
        followedUserId: String
    ): Boolean
}