package com.skintrack.server.config

import io.ktor.server.application.Application

data class AppConfig(
    val jwt: JwtConfig,
    val database: DatabaseConfig,
    val uploads: UploadsConfig,
)

data class JwtConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val expireMinutes: Long,
)

data class DatabaseConfig(
    val url: String,
    val user: String,
    val password: String,
)

data class UploadsConfig(
    val dir: String,
    val baseUrl: String,
)

fun Application.loadConfig(): AppConfig {
    val config = environment.config
    return AppConfig(
        jwt = JwtConfig(
            secret = config.property("app.jwt.secret").getString(),
            issuer = config.property("app.jwt.issuer").getString(),
            audience = config.property("app.jwt.audience").getString(),
            expireMinutes = config.property("app.jwt.expireMinutes").getString().toLong(),
        ),
        database = DatabaseConfig(
            url = config.property("app.database.url").getString(),
            user = config.property("app.database.user").getString(),
            password = config.property("app.database.password").getString(),
        ),
        uploads = UploadsConfig(
            dir = config.property("app.uploads.dir").getString(),
            baseUrl = config.property("app.uploads.baseUrl").getString(),
        ),
    )
}
