package com.daffa.data.repository.likes

import com.daffa.data.util.ParentType

interface LikeRepository {

    suspend fun likeParent(userId: String, parentId: String, parentType: Int): Boolean

    suspend fun unlikeParent(userId: String, parentId: String): Boolean

    suspend fun deleteLikesForParent(parentId: String)
}