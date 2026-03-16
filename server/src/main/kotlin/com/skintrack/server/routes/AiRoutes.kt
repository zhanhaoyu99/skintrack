@file:Suppress("DEPRECATION")

package com.skintrack.server.routes

import com.skintrack.server.dto.AttributionRequest
import com.skintrack.server.dto.SkinAnalysisRequest
import com.skintrack.server.dto.err
import com.skintrack.server.dto.ok
import com.skintrack.server.service.AiAnalysisService
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.contentType
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import java.util.Base64

fun Route.aiRoutes(aiService: AiAnalysisService) {
    authenticate("jwt") {
        route("/api/ai") {
            // POST /api/ai/analyze-skin
            // Accepts JSON { "image_base64": "...", "skin_type": "OILY" }
            // Or multipart form data with "image" file part and optional "skin_type" text part
            post("/analyze-skin") {
                val userId = call.principal<JWTPrincipal>()!!.payload.subject

                // Atomic rate limit check + increment
                if (!aiService.checkAndRecordUsage(userId)) {
                    call.respond(
                        HttpStatusCode.TooManyRequests,
                        err("Daily analysis limit reached. Please try again tomorrow.")
                    )
                    return@post
                }

                val contentType = call.request.contentType()
                val (imageBase64, skinType) = when {
                    contentType.match("multipart/form-data") -> {
                        parseMultipartRequest(call.receiveMultipart())
                    }
                    else -> {
                        val request = call.receive<SkinAnalysisRequest>()
                        request.imageBase64 to request.skinType
                    }
                }

                if (imageBase64.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, err("Image data is required"))
                    return@post
                }

                val result = aiService.analyzeSkin(imageBase64, skinType)
                call.respond(ok(result))
            }

            // POST /api/ai/attribution-report
            // Accepts JSON with records, products, and usages
            post("/attribution-report") {
                val userId = call.principal<JWTPrincipal>()!!.payload.subject

                if (!aiService.checkAndRecordUsage(userId)) {
                    call.respond(
                        HttpStatusCode.TooManyRequests,
                        err("Daily analysis limit reached. Please try again tomorrow.")
                    )
                    return@post
                }

                val request = call.receive<AttributionRequest>()

                if (request.records.size < 3) {
                    call.respond(HttpStatusCode.BadRequest, err("At least 3 skin records are required for attribution analysis"))
                    return@post
                }

                val result = aiService.generateAttributionReport(request)
                call.respond(ok(result))
            }
        }
    }
}

private suspend fun parseMultipartRequest(
    multipart: io.ktor.http.content.MultiPartData,
): Pair<String, String?> {
    var imageBase64 = ""
    var skinType: String? = null

    multipart.forEachPart { part ->
        when (part) {
            is PartData.FileItem -> {
                if (part.name == "image") {
                    val bytes = part.streamProvider().readBytes()
                    imageBase64 = Base64.getEncoder().encodeToString(bytes)
                }
            }
            is PartData.FormItem -> {
                if (part.name == "skin_type") {
                    skinType = part.value
                }
            }
            else -> {}
        }
        part.dispose()
    }

    return imageBase64 to skinType
}
