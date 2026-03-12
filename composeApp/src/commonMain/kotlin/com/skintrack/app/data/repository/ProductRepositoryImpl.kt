package com.skintrack.app.data.repository

import com.skintrack.app.data.local.dao.DailyProductUsageDao
import com.skintrack.app.data.local.dao.SkincareProductDao
import com.skintrack.app.data.local.entity.DailyProductUsageEntity
import com.skintrack.app.data.local.entity.SkincareProductEntity
import com.skintrack.app.data.remote.SupabaseSyncService
import com.skintrack.app.domain.model.DailyProductUsage
import com.skintrack.app.domain.model.ProductCategory
import com.skintrack.app.domain.model.SkincareProduct
import com.skintrack.app.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

class ProductRepositoryImpl(
    private val productDao: SkincareProductDao,
    private val usageDao: DailyProductUsageDao,
    private val syncService: SupabaseSyncService? = null,
) : ProductRepository {

    override fun getAllProducts(): Flow<List<SkincareProduct>> =
        productDao.getAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getProductById(id: String): SkincareProduct? =
        productDao.getById(id)?.toDomain()

    override suspend fun saveProduct(product: SkincareProduct) {
        productDao.insert(product.toEntity())
    }

    override suspend fun deleteProduct(product: SkincareProduct) {
        productDao.delete(product.toEntity())
    }

    override fun getUsageByDate(userId: String, date: LocalDate): Flow<List<DailyProductUsage>> {
        val epochMs = date.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        return usageDao.getUsageByDate(userId, epochMs).map { entities ->
            entities.map { it.toDomain(date) }
        }
    }

    override fun getUsageBetween(
        userId: String,
        startDate: LocalDate,
        endDate: LocalDate,
    ): Flow<List<DailyProductUsage>> {
        val startMs = startDate.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        val endMs = endDate.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        return usageDao.getUsageBetween(userId, startMs, endMs).map { entities ->
            entities.map { entity ->
                val epochMs = entity.usedDate
                val instant = Instant.fromEpochMilliseconds(epochMs)
                val date = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
                entity.toDomain(date)
            }
        }
    }

    override suspend fun logUsage(usage: DailyProductUsage) {
        usageDao.insert(usage.toEntity())
    }

    override suspend fun removeUsage(userId: String, productId: String, date: LocalDate) {
        val epochMs = date.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        usageDao.deleteByUserAndProductAndDate(userId, productId, epochMs)
    }

    override suspend fun syncToRemote() {
        val service = syncService ?: return
        try {
            val unsyncedProducts = productDao.getUnsynced()
            if (unsyncedProducts.isNotEmpty()) {
                val userId = unsyncedProducts.first().userId
                service.uploadProducts(unsyncedProducts, userId)
                unsyncedProducts.forEach { productDao.markSynced(it.id) }
            }

            val unsyncedUsage = usageDao.getUnsynced()
            if (unsyncedUsage.isNotEmpty()) {
                service.uploadUsage(unsyncedUsage)
                unsyncedUsage.forEach { usageDao.markSynced(it.id) }
            }
        } catch (_: Exception) {
            // Sync failure is non-fatal
        }
    }

    override suspend fun pullFromRemote(userId: String) {
        val service = syncService ?: return
        try {
            val remote = service.loadProducts(userId)
            remote.forEach { productDao.insert(it) }
        } catch (_: Exception) {
            // Pull failure is non-fatal
        }
    }
}

private fun SkincareProductEntity.toDomain() = SkincareProduct(
    id = id,
    userId = userId,
    name = name,
    brand = brand,
    category = runCatching { ProductCategory.valueOf(category) }.getOrDefault(ProductCategory.OTHER),
    imageUrl = imageUrl,
    barcode = barcode,
    synced = synced,
)

private fun SkincareProduct.toEntity() = SkincareProductEntity(
    id = id,
    userId = userId,
    name = name,
    brand = brand,
    category = category.name,
    imageUrl = imageUrl,
    barcode = barcode,
    synced = synced,
)

private fun DailyProductUsageEntity.toDomain(date: LocalDate) = DailyProductUsage(
    id = id,
    userId = userId,
    productId = productId,
    usedDate = date,
    synced = synced,
)

private fun DailyProductUsage.toEntity() = DailyProductUsageEntity(
    id = id,
    userId = userId,
    productId = productId,
    usedDate = usedDate.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
    synced = synced,
)
