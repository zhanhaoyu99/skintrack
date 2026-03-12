package com.skintrack.server.routes

import com.skintrack.server.dto.LoginRequest
import com.skintrack.server.dto.RegisterRequest
import com.skintrack.server.dto.ok
import com.skintrack.server.service.UserService
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes(userService: UserService) {
    route("/api/auth") {
        post("/register") {
            val request = call.receive<RegisterRequest>()
            val response = userService.register(request.email, request.password)
            call.respond(ok(response))
        }

        post("/login") {
            val request = call.receive<LoginRequest>()
            val response = userService.login(request.email, request.password)
            call.respond(ok(response))
        }
    }
}
