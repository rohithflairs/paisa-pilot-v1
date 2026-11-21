# Paisa Pilot - ProGuard Rules
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.paisapilot11819.data.backup.** { *; }
-dontwarn androidx.compose.**

