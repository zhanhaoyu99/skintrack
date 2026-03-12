package com.skintrack.server

import com.skintrack.server.auth.JwtService
import com.skintrack.server.config.loadConfig
import com.skintrack.server.database.DatabaseFactory
import com.skintrack.server.plugins.configureAuthentication
import com.skintrack.server.plugins.configureSerialization
import com.skintrack.server.plugins.configureStatusPages
import com.skintrack.server.routes.authRoutes
import com.skintrack.server.routes.imageRoutes
import com.skintrack.server.routes.productRoutes
import com.skintrack.server.routes.skinRecordRoutes
import com.skintrack.server.routes.subscriptionRoutes
import com.skintrack.server.service.ImageService
import com.skintrack.server.service.ProductService
import com.skintrack.server.service.SkinRecordService
import com.skintrack.server.service.SubscriptionService
import com.skintrack.server.service.UserService
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.http.content.staticFiles
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.routing.routing
import java.io.File

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val config = loadConfig()

    // Database
    DatabaseFactory.init(config.database)

    // Services
    val jwtService = JwtService(config.jwt)
    val userService = UserService(jwtService)
    val skinRecordService = SkinRecordService()
    val productService = ProductService()
    val imageService = ImageService(config.uploads)
    val subscriptionService = SubscriptionService()

    // Plugins
    configureSerialization()
    configureStatusPages()
    configureAuthentication(jwtService)

    install(CallLogging)
    install(CORS) {
        anyHost()
        allowHeader(io.ktor.http.HttpHeaders.ContentType)
        allowHeader(io.ktor.http.HttpHeaders.Authorization)
    }

    // Routes
    routing {
        authRoutes(userService)
        skinRecordRoutes(skinRecordService)
        productRoutes(productService)
        imageRoutes(imageService)
        subscriptionRoutes(subscriptionService)

        // Static files for uploaded images
        staticFiles("/uploads", File(config.uploads.dir))
    }
}
