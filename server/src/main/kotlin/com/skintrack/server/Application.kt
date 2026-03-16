package com.skintrack.server

import com.skintrack.server.auth.JwtService
import com.skintrack.server.config.loadConfig
import com.skintrack.server.database.DatabaseFactory
import com.skintrack.server.plugins.configureAuthentication
import com.skintrack.server.plugins.configureSerialization
import com.skintrack.server.plugins.configureStatusPages
import com.skintrack.server.routes.aiRoutes
import com.skintrack.server.routes.authRoutes
import com.skintrack.server.routes.imageRoutes
import com.skintrack.server.routes.notificationRoutes
import com.skintrack.server.routes.productRoutes
import com.skintrack.server.routes.skinRecordRoutes
import com.skintrack.server.routes.subscriptionRoutes
import com.skintrack.server.service.AiAnalysisService
import com.skintrack.server.service.ImageService
import com.skintrack.server.service.ProductService
import com.skintrack.server.service.SkinRecordService
import com.skintrack.server.service.SubscriptionService
import com.skintrack.server.service.UserService
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.client.plugins.HttpTimeout
import io.ktor.serialization.kotlinx.json.json as clientJson
import kotlinx.serialization.json.Json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.http.content.staticFiles
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.ratelimit.RateLimit
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.io.File
import kotlin.time.Duration.Companion.minutes

val AUTH_RATE_LIMIT = RateLimitName("auth")
val API_RATE_LIMIT = RateLimitName("api")

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val config = loadConfig()

    // Database
    DatabaseFactory.init(config.database)

    // Ktor HttpClient for outgoing LLM API calls
    val llmHttpClient = HttpClient {
        install(ClientContentNegotiation) {
            clientJson(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 120_000 // LLM calls can be slow
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 120_000
        }
    }

    // Services
    val jwtService = JwtService(config.jwt)
    val userService = UserService(jwtService)
    val skinRecordService = SkinRecordService()
    val productService = ProductService()
    val imageService = ImageService(config.uploads)
    val subscriptionService = SubscriptionService()
    val aiAnalysisService = AiAnalysisService(config.ai, llmHttpClient)

    // Plugins
    configureSerialization()
    configureStatusPages()
    configureAuthentication(jwtService)

    install(CallLogging)

    install(DefaultHeaders) {
        header("X-Content-Type-Options", "nosniff")
        header("X-Frame-Options", "DENY")
        header("Strict-Transport-Security", "max-age=31536000; includeSubDomains")
        header("X-XSS-Protection", "1; mode=block")
    }

    install(CORS) {
        config.cors.allowedHosts.forEach { host ->
            allowHost(host, schemes = listOf("http", "https"))
        }
        allowHeader(io.ktor.http.HttpHeaders.ContentType)
        allowHeader(io.ktor.http.HttpHeaders.Authorization)
    }

    install(RateLimit) {
        register(AUTH_RATE_LIMIT) {
            rateLimiter(limit = 5, refillPeriod = 1.minutes)
        }
        register(API_RATE_LIMIT) {
            rateLimiter(limit = 60, refillPeriod = 1.minutes)
        }
    }

    // Routes
    routing {
        get("/health") {
            call.respond(HttpStatusCode.OK, mapOf("status" to "ok", "version" to System.getProperty("app.version", "1.0.0")))
        }

        rateLimit(AUTH_RATE_LIMIT) {
            authRoutes(userService)
        }
        rateLimit(API_RATE_LIMIT) {
            skinRecordRoutes(skinRecordService)
            productRoutes(productService)
            imageRoutes(imageService)
            subscriptionRoutes(subscriptionService)
            notificationRoutes()
            aiRoutes(aiAnalysisService)
        }

        // Static files for uploaded images
        staticFiles("/uploads", File(config.uploads.dir))
    }
}
