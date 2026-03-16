package com.skintrack.server.routes

import com.skintrack.server.dto.DeleteAccountRequest
import com.skintrack.server.dto.LoginRequest
import com.skintrack.server.dto.PasswordChangeRequest
import com.skintrack.server.dto.PasswordResetRequest
import com.skintrack.server.dto.PasswordResetVerifyRequest
import com.skintrack.server.dto.ProfileUpdateRequest
import com.skintrack.server.dto.RefreshTokenRequest
import com.skintrack.server.dto.RegisterRequest
import com.skintrack.server.dto.ok
import com.skintrack.server.service.UserService
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
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

        post("/token/refresh") {
            val request = call.receive<RefreshTokenRequest>()
            val response = userService.refreshToken(request.refreshToken)
            call.respond(ok(response))
        }

        post("/password/reset") {
            val request = call.receive<PasswordResetRequest>()
            userService.requestPasswordReset(request.email)
            call.respond(ok("验证码已发送"))
        }

        post("/password/verify-reset") {
            val request = call.receive<PasswordResetVerifyRequest>()
            userService.resetPassword(request.email, request.code, request.newPassword)
            call.respond(ok("密码重置成功"))
        }

        authenticate("jwt") {
            post("/password/change") {
                val userId = call.principal<JWTPrincipal>()!!.payload.subject
                val request = call.receive<PasswordChangeRequest>()
                userService.changePassword(userId, request.oldPassword, request.newPassword)
                call.respond(ok("密码修改成功"))
            }

            delete("/user") {
                val userId = call.principal<JWTPrincipal>()!!.payload.subject
                val request = call.receive<DeleteAccountRequest>()
                userService.deleteAccount(userId, request.password)
                call.respond(ok("账户已删除"))
            }
        }
    }

    route("/api/user") {
        authenticate("jwt") {
            put("/profile") {
                val userId = call.principal<JWTPrincipal>()!!.payload.subject
                val request = call.receive<ProfileUpdateRequest>()
                userService.updateProfile(userId, request.displayName, request.skinType)
                    .onSuccess { call.respond(ok("资料更新成功")) }
                    .onFailure { throw it }
            }

            get("/export") {
                val userId = call.principal<JWTPrincipal>()!!.payload.subject
                val data = userService.exportUserData(userId)
                call.respond(ok(data))
            }
        }
    }
}
