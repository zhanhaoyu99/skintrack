package com.skintrack.server.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.skintrack.server.config.JwtConfig
import java.util.Date

class JwtService(private val config: JwtConfig) {

    val algorithm: Algorithm = Algorithm.HMAC256(config.secret)
    val issuer: String = config.issuer
    val audience: String = config.audience

    fun generateToken(userId: String, email: String): String =
        JWT.create()
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .withSubject(userId)
            .withClaim("email", email)
            .withClaim("type", "access")
            .withExpiresAt(Date(System.currentTimeMillis() + config.expireMinutes * 60 * 1000))
            .sign(algorithm)

    fun generateRefreshToken(userId: String, email: String): String =
        JWT.create()
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .withSubject(userId)
            .withClaim("email", email)
            .withClaim("type", "refresh")
            .withExpiresAt(Date(System.currentTimeMillis() + config.refreshExpireMinutes * 60 * 1000))
            .sign(algorithm)

    private val refreshExpireMinutes: Long get() = config.refreshExpireMinutes
}
