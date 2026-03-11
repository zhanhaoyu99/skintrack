package com.skintrack.app.domain.model

data class AuthUser(
    val userId: String,
    val email: String,
    val displayName: String? = null,
)
