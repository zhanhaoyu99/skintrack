package com.skintrack.app.di

import com.skintrack.app.data.local.AppDatabase
import com.skintrack.app.data.local.getDatabaseBuilder
import com.skintrack.app.data.remote.AiAnalysisService
import com.skintrack.app.data.remote.KtorServerConfig
import com.skintrack.app.data.remote.KtorSyncService
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
import com.skintrack.app.ui.screen.camera.CameraViewModel
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
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
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
            install(Logging) {
                level = LogLevel.BODY
            }
            if (KtorServerConfig.isConfigured) {
                install(Auth) {
                    bearer {
                        loadTokens {
                            tokenHolder.token?.let { BearerTokens(it, "") }
                        }
                    }
                }
            }
        }
    }

    // Supabase
    single { SupabaseProvider.client }

    // Remote sync service
    single<RemoteSyncService?> {
        when {
            KtorServerConfig.isConfigured -> KtorSyncService(get(), KtorServerConfig.baseUrl)
            SupabaseProvider.isConfigured -> SupabaseSyncService(get())
            else -> null
        }
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

    // Sync
    single { SyncManager(get(), get(), get()) }

    // Use Cases
    single { CheckFeatureAccess(get(), get()) }
    single { UpdateCheckInStreak(get(), get()) }

    // Platform
    single { ImageCompressor() }
    single { ImageStorage() }
    single { PaymentManager() }
    single { ShareManager() }
    single { NotificationManager() }

    // ViewModels
    viewModelOf(::AuthViewModel)
    viewModelOf(::AttributionReportViewModel)
    viewModelOf(::CameraViewModel)
    viewModelOf(::PaywallViewModel)
    viewModelOf(::ProductViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::RecordDetailViewModel)
    viewModelOf(::ShareCardViewModel)
    viewModelOf(::TimelineViewModel)
}

/** Holds the JWT token in memory for Bearer auth. */
class TokenHolder {
    @Volatile
    var token: String? = null
}
