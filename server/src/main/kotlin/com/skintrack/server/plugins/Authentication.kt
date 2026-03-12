package com.skintrack.server.plugins

import com.skintrack.server.auth.JwtService
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt

fun Application.configureAuthentication(jwtService: JwtService) {
    install(Authentication) {
        jwt("jwt") {
            realm = "skintrack"
            verifier(
                com.auth0.jwt.JWT.require(jwtService.algorithm)
                    .withIssuer(jwtService.issuer)
                    .withAudience(jwtService.audience)
                    .build()
            )
            validate { credential ->
                val userId = credential.payload.subject
                if (userId != null) JWTPrincipal(credential.payload) else null
            }
        }
    }
}
