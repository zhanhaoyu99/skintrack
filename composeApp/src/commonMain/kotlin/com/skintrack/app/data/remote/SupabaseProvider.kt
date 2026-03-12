package com.skintrack.app.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseProvider {

    // Credentials loaded from BuildConfig (set via local.properties or CI env)
    // Fallback to placeholder values for development builds
    private val supabaseUrl: String
        get() = try {
            Class.forName("com.skintrack.app.BuildConfig")
                .getField("SUPABASE_URL").get(null) as String
        } catch (_: Exception) {
            "https://YOUR_PROJECT.supabase.co"
        }

    private val supabaseAnonKey: String
        get() = try {
            Class.forName("com.skintrack.app.BuildConfig")
                .getField("SUPABASE_ANON_KEY").get(null) as String
        } catch (_: Exception) {
            "YOUR_ANON_KEY"
        }

    val isConfigured: Boolean
        get() = !supabaseUrl.contains("YOUR_PROJECT")

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = supabaseUrl,
            supabaseKey = supabaseAnonKey,
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
        }
    }
}
