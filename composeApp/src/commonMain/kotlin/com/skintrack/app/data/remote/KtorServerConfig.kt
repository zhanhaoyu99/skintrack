package com.skintrack.app.data.remote

object KtorServerConfig {

    private val serverUrl: String
        get() = try {
            Class.forName("com.skintrack.app.BuildConfig")
                .getField("KTOR_SERVER_URL").get(null) as String
        } catch (_: Exception) {
            "http://10.0.2.2:8080" // Android emulator localhost
        }

    val isConfigured: Boolean
        get() = serverUrl.isNotBlank() && !serverUrl.contains("YOUR_SERVER")

    val baseUrl: String get() = serverUrl
}
