package com.skintrack.server.config

import io.ktor.server.application.Application

data class AppConfig(
    val jwt: JwtConfig,
    val database: DatabaseConfig,
    val uploads: UploadsConfig,
    val cors: CorsConfig,
    val ai: AiConfig,
)

data class JwtConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val expireMinutes: Long,
    val refreshExpireMinutes: Long,
) {
    override fun toString() = "JwtConfig(issuer=$issuer, audience=$audience, secret=***)"
}

data class DatabaseConfig(
    val url: String,
    val user: String,
    val password: String,
) {
    override fun toString() = "DatabaseConfig(url=$url, user=$user, password=***)"
}

data class UploadsConfig(
    val dir: String,
    val baseUrl: String,
)

data class CorsConfig(
    val allowedHosts: List<String>,
)

data class AiConfig(
    val provider: String, // "openai", "gemini", "claude"
    val apiKey: String,
    val model: String,
    val maxDailyAnalysesPerUser: Int = 10,
) {
    val isConfigured: Boolean get() = apiKey.isNotBlank() && apiKey != "YOUR_API_KEY"
    override fun toString() = "AiConfig(provider=$provider, model=$model, apiKey=***)"
}

fun Application.loadConfig(): AppConfig {
    val config = environment.config
    return AppConfig(
        jwt = JwtConfig(
            secret = config.property("app.jwt.secret").getString(),
            issuer = config.property("app.jwt.issuer").getString(),
            audience = config.property("app.jwt.audience").getString(),
            expireMinutes = config.property("app.jwt.expireMinutes").getString().toLong(),
            refreshExpireMinutes = config.property("app.jwt.refreshExpireMinutes").getString().toLong(),
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
        cors = CorsConfig(
            allowedHosts = config.property("app.cors.allowedHosts").getString()
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() },
        ),
        ai = AiConfig(
            provider = config.property("app.ai.provider").getString(),
            apiKey = config.property("app.ai.apiKey").getString(),
            model = config.property("app.ai.model").getString(),
            maxDailyAnalysesPerUser = config.property("app.ai.maxDailyAnalysesPerUser").getString().toInt(),
        ),
    )
}
