package com.skintrack.server.plugins

import com.skintrack.server.dto.err
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("StatusPages")

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            logger.warn("Bad request: {}", cause.message, cause)
            call.respond(HttpStatusCode.BadRequest, err("请求参数错误"))
        }
        exception<IllegalStateException> { call, cause ->
            logger.warn("Conflict: {}", cause.message, cause)
            call.respond(HttpStatusCode.Conflict, err("操作冲突"))
        }
        exception<Exception> { call, cause ->
            logger.error("Internal server error", cause)
            call.respond(HttpStatusCode.InternalServerError, err("服务器内部错误"))
        }
    }
}
