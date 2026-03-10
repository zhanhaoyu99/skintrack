package com.skintrack.app.data.repository

import com.skintrack.app.data.local.dao.DailyProductUsageDao
import com.skintrack.app.data.local.dao.SkincareProductDao
import com.skintrack.app.data.local.entity.DailyProductUsageEntity
import com.skintrack.app.data.local.entity.SkincareProductEntity
import com.skintrack.app.domain.model.DailyProductUsage
import com.skintrack.app.domain.model.ProductCategory
import com.skintrack.app.domain.model.SkincareProduct
import com.skintrack.app.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.TimeZone

class ProductRepositoryImpl(
    private val productDao: SkincareProductDao,
    private val usageDao: DailyProductUsageDao,
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

    override suspend fun logUsage(usage: DailyProductUsage) {
        usageDao.insert(usage.toEntity())
    }

    override suspend fun syncToRemote() {
        // TODO: Sync unsynced products and usage to Supabase
    }
}

private fun SkincareProductEntity.toDomain() = SkincareProduct(
    id = id,
    name = name,
    brand = brand,
    category = runCatching { ProductCategory.valueOf(category) }.getOrDefault(ProductCategory.OTHER),
    imageUrl = imageUrl,
    barcode = barcode,
    synced = synced,
)

private fun SkincareProduct.toEntity() = SkincareProductEntity(
    id = id,
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
