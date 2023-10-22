package com.daffa.data.repository.activity

import com.daffa.data.models.Activity
import com.daffa.util.Constants

interface ActivityRepository {
    suspend fun getActivitiesForUser(
        userId: String,
        page: Int = 0,
        pageSize: Int = Constants.DEFAULT_ACTIVITY_PAGE_SIZE
    ): List<Activity>

    suspend fun createActivity(activity: Activity): Boolean

    suspend fun deleteActivity(activityId: String): Boolean
}