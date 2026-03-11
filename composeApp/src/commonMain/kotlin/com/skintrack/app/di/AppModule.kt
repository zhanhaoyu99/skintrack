package com.skintrack.app.di

import com.skintrack.app.data.local.AppDatabase
import com.skintrack.app.data.local.getDatabaseBuilder
import com.skintrack.app.data.remote.AiAnalysisService
import com.skintrack.app.data.repository.ProductRepositoryImpl
import com.skintrack.app.data.repository.SkinRecordRepositoryImpl
import com.skintrack.app.domain.repository.ProductRepository
import com.skintrack.app.domain.repository.SkinRecordRepository
import com.skintrack.app.platform.ImageCompressor
import com.skintrack.app.platform.ImageStorage
import com.skintrack.app.ui.screen.camera.CameraViewModel
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
        builder.build()
    }
    single { get<AppDatabase>().skinRecordDao() }
    single { get<AppDatabase>().skincareProductDao() }
    single { get<AppDatabase>().dailyProductUsageDao() }

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

    // Services
    single { AiAnalysisService(get()) }

    // Repositories
    single<SkinRecordRepository> { SkinRecordRepositoryImpl(get()) }
    single<ProductRepository> { ProductRepositoryImpl(get(), get()) }

    // Platform
    single { ImageCompressor() }
    single { ImageStorage() }

    // ViewModels
    viewModelOf(::CameraViewModel)
    viewModelOf(::TimelineViewModel)
}
