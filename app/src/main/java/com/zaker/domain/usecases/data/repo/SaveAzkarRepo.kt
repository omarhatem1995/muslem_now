package com.zaker.domain.usecases.data.repo

import com.zaker.data.entities.model.AzkarModel

interface SaveAzkarRepo {
    suspend fun SaveAzkar(azkar: AzkarModel)
}