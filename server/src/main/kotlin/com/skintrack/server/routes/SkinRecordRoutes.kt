package com.skintrack.server.routes

import com.skintrack.server.dto.SkinRecordDto
import com.skintrack.server.dto.ok
import com.skintrack.server.service.SkinRecordService
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.skinRecordRoutes(service: SkinRecordService) {
    authenticate("jwt") {
        route("/api/skin-records") {
            get {
                val userId = call.principal<JWTPrincipal>()!!.payload.subject
                val records = service.loadByUser(userId)
                call.respond(ok(records))
            }

            post {
                val userId = call.principal<JWTPrincipal>()!!.payload.subject
                val dtos = call.receive<List<SkinRecordDto>>()
                service.upsert(dtos, userId)
                call.respond(ok("synced"))
            }
        }
    }
}
