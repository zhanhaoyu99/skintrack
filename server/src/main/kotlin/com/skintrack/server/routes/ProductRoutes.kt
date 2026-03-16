package com.skintrack.server.routes

import com.skintrack.server.dto.DailyProductUsageDto
import com.skintrack.server.dto.SkincareProductDto
import com.skintrack.server.dto.ok
import com.skintrack.server.service.ProductService
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.productRoutes(service: ProductService) {
    authenticate("jwt") {
        route("/api/products") {
            get {
                val userId = call.principal<JWTPrincipal>()!!.payload.subject
                val since = call.request.queryParameters["since"]
                val products = service.loadProducts(userId, since)
                call.respond(ok(products))
            }

            post {
                val userId = call.principal<JWTPrincipal>()!!.payload.subject
                val dtos = call.receive<List<SkincareProductDto>>()
                service.upsertProducts(dtos, userId)
                call.respond(ok("synced"))
            }
        }

        post("/api/usage") {
            val userId = call.principal<JWTPrincipal>()!!.payload.subject
            val dtos = call.receive<List<DailyProductUsageDto>>()
            service.upsertUsage(dtos, userId)
            call.respond(ok("synced"))
        }
    }
}
