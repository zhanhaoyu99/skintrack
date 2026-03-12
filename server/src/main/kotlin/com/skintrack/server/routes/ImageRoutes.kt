@file:Suppress("DEPRECATION")

package com.skintrack.server.routes

import com.skintrack.server.dto.ImageUploadResponse
import com.skintrack.server.dto.ok
import com.skintrack.server.service.ImageService
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.imageRoutes(service: ImageService) {
    authenticate("jwt") {
        post("/api/images/upload") {
            val userId = call.principal<JWTPrincipal>()!!.payload.subject
            val multipart = call.receiveMultipart()

            var imageUrl: String? = null
            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        val fileName = part.originalFileName ?: "image.jpg"
                        val bytes = part.streamProvider().readBytes()
                        imageUrl = service.save(userId, fileName, bytes)
                    }
                    else -> {}
                }
                part.dispose()
            }

            val url = imageUrl ?: throw IllegalArgumentException("未找到上传文件")
            call.respond(ok(ImageUploadResponse(imageUrl = url)))
        }
    }
}
