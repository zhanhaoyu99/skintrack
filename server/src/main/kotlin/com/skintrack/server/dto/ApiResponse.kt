package com.skintrack.server.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: String? = null,
)

fun <T> ok(data: T) = ApiResponse(success = true, data = data)
fun err(message: String) = ApiResponse<Unit>(success = false, error = message)
