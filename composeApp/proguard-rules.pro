# SkinTrack ProGuard Rules

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }
-keep,includedescriptorclasses class com.skintrack.app.**$$serializer { *; }
-keepclassmembers class com.skintrack.app.** { *** Companion; }
-keepclasseswithmembers class com.skintrack.app.** { kotlinx.serialization.KSerializer serializer(...); }

# Ktor
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Koin
-keep class org.koin.** { *; }
-dontwarn org.koin.**

# Supabase
-keep class io.github.jan.supabase.** { *; }
-dontwarn io.github.jan.supabase.**

# Coil
-keep class coil3.** { *; }
-dontwarn coil3.**

# Voyager
-keep class cafe.adriel.voyager.** { *; }
-dontwarn cafe.adriel.voyager.**

# Keep data classes used in API responses
-keep class com.skintrack.app.data.remote.dto.** { *; }
-keep class com.skintrack.app.domain.model.** { *; }
-keep class com.skintrack.app.data.local.entity.** { *; }

# OkHttp (used by Ktor client)
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# Napier (logging)
-keep class io.github.aakira.napier.** { *; }
-dontwarn io.github.aakira.napier.**

# CameraX
-keep class androidx.camera.** { *; }
-dontwarn androidx.camera.**

# Compose - keep Stability annotation
-keep class androidx.compose.runtime.** { *; }
