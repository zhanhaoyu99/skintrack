package com.skintrack.server.database

import com.skintrack.server.config.DatabaseConfig
import com.skintrack.server.database.tables.CheckInStreaksTable
import com.skintrack.server.database.tables.DailyProductUsageTable
import com.skintrack.server.database.tables.SkinRecordsTable
import com.skintrack.server.database.tables.SkincareProductsTable
import com.skintrack.server.database.tables.UserSubscriptionsTable
import com.skintrack.server.database.tables.UsersTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init(config: DatabaseConfig) {
        val dataSource = HikariDataSource(HikariConfig().apply {
            jdbcUrl = config.url
            username = config.user
            password = config.password
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        })

        Database.connect(dataSource)

        transaction {
            SchemaUtils.create(
                UsersTable,
                SkinRecordsTable,
                SkincareProductsTable,
                DailyProductUsageTable,
                UserSubscriptionsTable,
                CheckInStreaksTable,
            )
        }
    }
}
