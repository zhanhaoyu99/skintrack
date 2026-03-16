package com.skintrack.server.routes

import com.skintrack.server.database.tables.UsersTable
import com.skintrack.server.dto.DeviceTokenRequest
import com.skintrack.server.dto.ok
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

fun Route.notificationRoutes() {
    route("/api/user") {
        authenticate("jwt") {
            post("/device-token") {
                val userId = call.principal<JWTPrincipal>()!!.payload.subject
                val request = call.receive<DeviceTokenRequest>()

                transaction {
                    UsersTable.update({ UsersTable.id eq userId }) {
                        it[fcmToken] = request.token
                        it[devicePlatform] = request.platform
                    }
                }

                call.respond(ok("Device token saved"))
            }
        }
    }
}
