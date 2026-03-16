package com.skintrack.server.service

import com.skintrack.server.database.tables.DailyProductUsageTable
import com.skintrack.server.database.tables.SkincareProductsTable
import com.skintrack.server.dto.DailyProductUsageDto
import com.skintrack.server.dto.SkincareProductDto
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert

class ProductService {

    fun loadProducts(userId: String, since: String? = null): List<SkincareProductDto> = transaction {
        SkincareProductsTable.selectAll()
            .where {
                if (since != null) {
                    (SkincareProductsTable.userId eq userId) and
                        (SkincareProductsTable.updatedAt.isNotNull()) and
                        (SkincareProductsTable.updatedAt greater since)
                } else {
                    SkincareProductsTable.userId eq userId
                }
            }
            .map { row ->
                SkincareProductDto(
                    id = row[SkincareProductsTable.id],
                    userId = row[SkincareProductsTable.userId],
                    name = row[SkincareProductsTable.name],
                    brand = row[SkincareProductsTable.brand],
                    category = row[SkincareProductsTable.category],
                    imageUrl = row[SkincareProductsTable.imageUrl],
                    barcode = row[SkincareProductsTable.barcode],
                    createdAt = row[SkincareProductsTable.createdAt],
                    updatedAt = row[SkincareProductsTable.updatedAt],
                )
            }
    }

    fun upsertProducts(dtos: List<SkincareProductDto>, userId: String) = transaction {
        val now = Clock.System.now().toString()
        dtos.forEach { dto ->
            SkincareProductsTable.upsert {
                it[id] = dto.id
                it[SkincareProductsTable.userId] = userId // Force JWT userId
                it[name] = dto.name
                it[brand] = dto.brand
                it[category] = dto.category
                it[imageUrl] = dto.imageUrl
                it[barcode] = dto.barcode
                it[createdAt] = dto.createdAt
                it[updatedAt] = now
            }
        }
    }

    fun upsertUsage(dtos: List<DailyProductUsageDto>, userId: String) = transaction {
        dtos.forEach { dto ->
            DailyProductUsageTable.upsert {
                it[id] = dto.id
                it[DailyProductUsageTable.userId] = userId // Force JWT userId
                it[productId] = dto.productId
                it[usedDate] = dto.usedDate
                it[createdAt] = dto.createdAt
            }
        }
    }
}
