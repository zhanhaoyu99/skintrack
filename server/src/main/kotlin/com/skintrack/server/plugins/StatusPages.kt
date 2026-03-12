package com.skintrack.server.plugins

import com.skintrack.server.dto.err
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, err(cause.message ?: "请求参数错误"))
        }
        exception<IllegalStateException> { call, cause ->
            call.respond(HttpStatusCode.Conflict, err(cause.message ?: "操作冲突"))
        }
        exception<Exception> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, err(cause.message ?: "服务器内部错误"))
        }
    }
}
