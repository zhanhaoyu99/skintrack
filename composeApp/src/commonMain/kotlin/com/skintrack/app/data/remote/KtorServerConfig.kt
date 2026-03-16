package com.skintrack.app.data.remote

object KtorServerConfig {

    private val serverUrl: String
        get() = try {
            Class.forName("com.skintrack.app.BuildConfig")
                .getField("KTOR_SERVER_URL").get(null) as String
        } catch (_: Exception) {
            // No fallback URL — server URL must be configured via BuildConfig.
            // Never hardcode internal IPs here to avoid leaking network topology.
            ""
        }

    /** Whether a valid server URL has been configured via BuildConfig. */
    val isConfigured: Boolean
        get() = serverUrl.isNotBlank() && !serverUrl.contains("YOUR_SERVER")

    /** Returns the configured server base URL. Throws if not configured. */
    val baseUrl: String
        get() {
            check(isConfigured) {
                "Ktor server URL is not configured. Set KTOR_SERVER_URL in BuildConfig via local.properties."
            }
            return serverUrl
        }
}
