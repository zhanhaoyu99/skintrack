package com.skintrack.server.service

import com.skintrack.server.database.tables.DailyProductUsageTable
import com.skintrack.server.database.tables.SkincareProductsTable
import com.skintrack.server.dto.DailyProductUsageDto
import com.skintrack.server.dto.SkincareProductDto
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert

class ProductService {

    fun loadProducts(userId: String): List<SkincareProductDto> = transaction {
        SkincareProductsTable.selectAll()
            .where { SkincareProductsTable.userId eq userId }
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
                )
            }
    }

    fun upsertProducts(dtos: List<SkincareProductDto>, userId: String) = transaction {
        dtos.filter { it.userId == userId }.forEach { dto ->
            SkincareProductsTable.upsert {
                it[id] = dto.id
                it[SkincareProductsTable.userId] = dto.userId
                it[name] = dto.name
                it[brand] = dto.brand
                it[category] = dto.category
                it[imageUrl] = dto.imageUrl
                it[barcode] = dto.barcode
                it[createdAt] = dto.createdAt
            }
        }
    }

    fun upsertUsage(dtos: List<DailyProductUsageDto>, userId: String) = transaction {
        dtos.filter { it.userId == userId }.forEach { dto ->
            DailyProductUsageTable.upsert {
                it[id] = dto.id
                it[DailyProductUsageTable.userId] = dto.userId
                it[productId] = dto.productId
                it[usedDate] = dto.usedDate
                it[createdAt] = dto.createdAt
            }
        }
    }
}
