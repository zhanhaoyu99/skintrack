package com.skintrack.app.domain.repository

import com.skintrack.app.domain.model.DailyProductUsage
import com.skintrack.app.domain.model.SkincareProduct
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface ProductRepository {
    fun getAllProducts(): Flow<List<SkincareProduct>>
    suspend fun getProductById(id: String): SkincareProduct?
    suspend fun saveProduct(product: SkincareProduct)
    suspend fun deleteProduct(product: SkincareProduct)
    fun getUsageByDate(userId: String, date: LocalDate): Flow<List<DailyProductUsage>>
    fun getUsageBetween(userId: String, startDate: LocalDate, endDate: LocalDate): Flow<List<DailyProductUsage>>
    suspend fun logUsage(usage: DailyProductUsage)
    suspend fun removeUsage(userId: String, productId: String, date: LocalDate)
    suspend fun syncToRemote()
}
