# Keep generic annotations
-keepattributes *Annotation*, InnerClasses, Signature, EnclosingMethod

# Kotlin metadata
-dontwarn kotlin.**

# Moshi
-keep class com.squareup.moshi.** { *; }
-keep @com.squareup.moshi.JsonQualifier interface *
-keepclassmembers class * {
    @com.squareup.moshi.Json <fields>;
}
-keep class **JsonAdapter { *; }
-keepnames @com.squareup.moshi.JsonClass class *

# Retrofit / OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-keepattributes Exceptions
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# App models used with reflection-free moshi codegen are safe, keep data classes anyway
-keep class com.claudio.memoria.data.remote.** { *; }
-keep class com.claudio.memoria.data.content.** { *; }
