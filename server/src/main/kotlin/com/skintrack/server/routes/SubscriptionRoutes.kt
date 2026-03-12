package com.skintrack.server.routes

import com.skintrack.server.dto.CheckInStreakDto
import com.skintrack.server.dto.UserSubscriptionDto
import com.skintrack.server.dto.ok
import com.skintrack.server.service.SubscriptionService
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.subscriptionRoutes(service: SubscriptionService) {
    authenticate("jwt") {
        route("/api/subscription") {
            get {
                val userId = call.principal<JWTPrincipal>()!!.payload.subject
                val sub = service.loadSubscription(userId)
                call.respond(ok(sub))
            }

            put {
                val dto = call.receive<UserSubscriptionDto>()
                val userId = call.principal<JWTPrincipal>()!!.payload.subject
                service.updateSubscription(dto.copy(userId = userId))
                call.respond(ok("updated"))
            }
        }

        route("/api/streak") {
            get {
                val userId = call.principal<JWTPrincipal>()!!.payload.subject
                val streak = service.loadStreak(userId)
                call.respond(ok(streak))
            }

            put {
                val dto = call.receive<CheckInStreakDto>()
                val userId = call.principal<JWTPrincipal>()!!.payload.subject
                service.updateStreak(dto.copy(userId = userId))
                call.respond(ok("updated"))
            }
        }
    }
}
