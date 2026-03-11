package com.skintrack.app

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.skintrack.app.di.appModule
import org.koin.core.context.startKoin

class SkinTrackApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        com.skintrack.app.data.local.applicationContext = this
        startKoin {
            modules(appModule)
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}
