package com.skintrack.app.di

import com.skintrack.app.data.local.AppDatabase
import com.skintrack.app.data.local.getDatabaseBuilder
import com.skintrack.app.data.remote.AiAnalysisService
import com.skintrack.app.data.remote.SupabaseProvider
import com.skintrack.app.data.remote.SupabaseSyncService
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

    // Network
    single {
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
        }
    }

    // Supabase
    single { SupabaseProvider.client }

    // Sync service (null when Supabase not configured)
    single<SupabaseSyncService?> {
        if (SupabaseProvider.isConfigured) SupabaseSyncService(get()) else null
    }

    // Services
    single { AiAnalysisService(get()) }

    // Repositories
    single<AuthRepository> {
        if (SupabaseProvider.isConfigured) {
            SupabaseAuthRepository(get(), get())
        } else {
            MockAuthRepository(get())
        }
    }
    single<SkinRecordRepository> { SkinRecordRepositoryImpl(get(), getOrNull()) }
    single<ProductRepository> { ProductRepositoryImpl(get(), get(), getOrNull()) }
    single<SubscriptionRepository> { SubscriptionRepositoryImpl(get(), get(), get(), get()) }

    // Use Cases
    single { CheckFeatureAccess(get()) }
    single { UpdateCheckInStreak(get()) }

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
