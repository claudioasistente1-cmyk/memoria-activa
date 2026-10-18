package com.claudio.memoria.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateManifest(
    @Json(name = "versionCode") val versionCode: Int = 0,
    @Json(name = "versionName") val versionName: String = "",
    @Json(name = "apkUrl") val apkUrl: String = "",
    @Json(name = "sha256") val sha256: String = "",
    @Json(name = "releaseNotes") val releaseNotes: String = "",
    @Json(name = "apkSizeBytes") val apkSizeBytes: Long = 0,
    @Json(name = "minSdk") val minSdk: Int = 28,
    @Json(name = "publishedAt") val publishedAt: String = ""
) {
    fun isUsable(): Boolean =
        versionCode > 0 &&
            versionName.isNotBlank() &&
            apkUrl.startsWith("https://") &&
            sha256.length == 64
}
