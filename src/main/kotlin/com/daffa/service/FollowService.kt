package com.daffa.service

import com.daffa.data.repository.follow.FollowRepository
import com.daffa.data.requests.FollowUpdateRequest

class FollowService(
    private val followRepository: FollowRepository
) {

    suspend fun followUserIfExist(request: FollowUpdateRequest, followingUserId: String): Boolean {
        return followRepository.followUserIfExists(
            followingUserId = followingUserId,
            followedUserId = request.followedUserId
        )
    }

    suspend fun unfollowUserIfExist(request: FollowUpdateRequest, followingUserId: String): Boolean {
        return followRepository.unfollowUserIfExists(
            followingUserId = followingUserId,
            followedUserId = request.followedUserId
        )
    }
}