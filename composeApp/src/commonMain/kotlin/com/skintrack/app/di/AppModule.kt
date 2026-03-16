package com.skintrack.app.di

import com.skintrack.app.data.local.AppDatabase
import com.skintrack.app.data.local.getDatabaseBuilder
import com.skintrack.app.data.remote.AiAnalysisService
import com.skintrack.app.data.remote.KtorServerConfig
import com.skintrack.app.data.remote.KtorSyncService
import com.skintrack.app.data.remote.NetworkMonitor
import com.skintrack.app.data.remote.RemoteSyncService
import com.skintrack.app.data.remote.SupabaseProvider
import com.skintrack.app.data.remote.SupabaseSyncService
import com.skintrack.app.data.remote.SyncManager
import com.skintrack.app.data.repository.KtorAuthRepository
import com.skintrack.app.data.repository.MockAuthRepository
import com.skintrack.app.data.repository.ProductRepositoryImpl
import com.skintrack.app.data.repository.SkinRecordRepositoryImpl
import com.skintrack.app.data.repository.SupabaseAuthRepository
import com.skintrack.app.data.repository.SubscriptionRepositoryImpl
import com.skintrack.app.domain.repository.AuthRepository
import com.skintrack.app.domain.repository.ProductRepository
import com.skintrack.app.domain.repository.SkinRecordRepository
import com.skintrack.app.domain.repository.SubscriptionRepository
import com.skintrack.app.domain.usecase.CheckFeatureAccess
import com.skintrack.app.domain.usecase.UpdateCheckInStreak
import com.skintrack.app.platform.ImageCompressor
import com.skintrack.app.platform.ImageStorage
import com.skintrack.app.platform.NotificationManager
import com.skintrack.app.platform.PaymentManager
import com.skintrack.app.platform.ShareManager
import com.skintrack.app.ui.screen.attribution.AttributionReportViewModel
import com.skintrack.app.ui.screen.auth.AuthViewModel
import com.skintrack.app.ui.screen.auth.ForgotPasswordViewModel
import com.skintrack.app.ui.screen.camera.CameraViewModel
import com.skintrack.app.ui.screen.dashboard.DashboardViewModel
import com.skintrack.app.ui.screen.onboarding.OnboardingViewModel
import com.skintrack.app.ui.screen.settings.ChangePasswordViewModel
import com.skintrack.app.ui.screen.settings.EditProfileViewModel
import com.skintrack.app.ui.screen.settings.SettingsViewModel
import com.skintrack.app.ui.screen.paywall.PaywallViewModel
import com.skintrack.app.ui.screen.product.ProductViewModel
import com.skintrack.app.ui.screen.profile.ProfileViewModel
import com.skintrack.app.ui.screen.report.RecordDetailViewModel
import com.skintrack.app.ui.screen.share.ShareCardViewModel
import com.skintrack.app.ui.screen.timeline.TimelineViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import org.koin.mp.KoinPlatform
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.HttpTimeout
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    // Database
    single {
        val builder = getDatabaseBuilder()
        builder.fallbackToDestructiveMigration(true).build()
    }
    single { get<AppDatabase>().skinRecordDao() }
    single { get<AppDatabase>().skincareProductDao() }
    single { get<AppDatabase>().dailyProductUsageDao() }
    single { get<AppDatabase>().authSessionDao() }
    single { get<AppDatabase>().userSubscriptionDao() }
    single { get<AppDatabase>().checkInStreakDao() }
    single { get<AppDatabase>().syncQueueDao() }
    single { get<AppDatabase>().userPreferencesDao() }

    // JWT token holder for Ktor backend
    single { TokenHolder() }

    // Network
    single {
        val tokenHolder: TokenHolder = get()
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = false
                })
            }
            // Production: use LogLevel.NONE to avoid leaking sensitive data
            install(Logging) {
                level = LogLevel.INFO
                logger = object : Logger {
                    override fun log(message: String) {
                        // Filter out Authorization headers to prevent token leakage
                        val sanitized = message.replace(
                            Regex("(Authorization:\\s*)\\S+", RegexOption.IGNORE_CASE),
                            "$1[REDACTED]"
                        )
                        io.github.aakira.napier.Napier.d(sanitized, tag = "HttpClient")
                    }
                }
            }
            install(HttpTimeout) {
                connectTimeoutMillis = 15_000
                requestTimeoutMillis = 30_000
                socketTimeoutMillis = 30_000
            }
            if (KtorServerConfig.isConfigured) {
                install(Auth) {
                    bearer {
                        loadTokens {
                            tokenHolder.token?.let { BearerTokens(it, "") }
                        }
                        refreshTokens {
                            // Attempt to refresh the access token on 401
                            try {
                                val authRepo = KoinPlatform.getKoin().get<AuthRepository>()
                                val result = authRepo.refreshAccessToken()
                                if (result.isSuccess) {
                                    tokenHolder.token?.let { BearerTokens(it, "") }
                                } else {
                                    // Refresh failed, force logout
                                    authRepo.logout()
                                    null
                                }
                            } catch (_: Exception) {
                                try {
                                    KoinPlatform.getKoin().get<AuthRepository>().logout()
                                } catch (_: Exception) { /* ignore */ }
                                null
                            }
                        }
                    }
                }
            }
        }
    }

    // Supabase
    single { SupabaseProvider.client }

    // Remote sync service — only registered when a backend is configured.
    // Use getOrNull<RemoteSyncService>() to safely resolve.
    if (KtorServerConfig.isConfigured) {
        single<RemoteSyncService> { KtorSyncService(get(), KtorServerConfig.baseUrl) }
    } else if (SupabaseProvider.isConfigured) {
        single<RemoteSyncService> { SupabaseSyncService(get()) }
    }

    // Services
    single { AiAnalysisService(get()) }

    // Repositories
    single<AuthRepository> {
        when {
            KtorServerConfig.isConfigured -> {
                val tokenHolder: TokenHolder = get()
                KtorAuthRepository(get(), KtorServerConfig.baseUrl, get()) { token ->
                    tokenHolder.token = token.ifBlank { null }
                }
            }
            SupabaseProvider.isConfigured -> SupabaseAuthRepository(get(), get())
            else -> MockAuthRepository(get())
        }
    }
    single<SkinRecordRepository> { SkinRecordRepositoryImpl(get(), getOrNull()) }
    single<ProductRepository> { ProductRepositoryImpl(get(), get(), getOrNull()) }
    single<SubscriptionRepository> { SubscriptionRepositoryImpl(get(), get(), get(), get()) }

    // Network Monitor
    single { NetworkMonitor() }

    // Platform
    single { ImageCompressor() }
    single { ImageStorage() }

    // Sync
    single {
        SyncManager(
            authRepository = get(),
            skinRecordRepository = get(),
            productRepository = get(),
            syncQueueDao = get(),
            userPreferencesDao = get(),
            networkMonitor = get(),
            aiAnalysisService = get<AiAnalysisService>(),
            imageStorage = get<ImageStorage>(),
        )
    }

    // Use Cases
    single { CheckFeatureAccess(get(), get()) }
    single { UpdateCheckInStreak(get(), get()) }
    single { PaymentManager() }
    single { ShareManager() }
    single { NotificationManager() }

    // ViewModels
    viewModelOf(::AuthViewModel)
    viewModelOf(::ForgotPasswordViewModel)
    viewModelOf(::AttributionReportViewModel)
    viewModelOf(::CameraViewModel)
    viewModelOf(::DashboardViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::ChangePasswordViewModel)
    viewModelOf(::EditProfileViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::PaywallViewModel)
    viewModelOf(::ProductViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::RecordDetailViewModel)
    viewModelOf(::ShareCardViewModel)
    viewModelOf(::TimelineViewModel)
}

/**
 * Holds the JWT token in memory for Bearer auth.
 *
 * TODO: For production, migrate to platform-specific secure storage:
 *  - Android: EncryptedSharedPreferences (androidx.security.crypto)
 *  - iOS: Keychain via expect/actual
 *  Current in-memory approach is acceptable for development but tokens are
 *  lost on process death and not encrypted at rest.
 */
class TokenHolder {
    @Volatile
    var token: String? = null
}
