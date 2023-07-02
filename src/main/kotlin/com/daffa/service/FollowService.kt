package com.daffa.service

import com.daffa.data.repository.follow.FollowRepository
import com.daffa.data.requests.FollowUpdateRequest

class FollowService(
    private val followRepository: FollowRepository
) {

    suspend fun followUserIfExist(request: FollowUpdateRequest): Boolean {
        return followRepository.followUserIfExists(
            followingUserId = request.followingUserId,
            followedUserId = request.followedUserId
        )
    }

    suspend fun unfollowUserIfExist(request: FollowUpdateRequest): Boolean {
        return followRepository.unfollowUserIfExists(
            followingUserId = request.followingUserId,
            followedUserId = request.followedUserId
        )
    }
}