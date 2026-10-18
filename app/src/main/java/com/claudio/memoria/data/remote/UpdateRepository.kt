package com.claudio.memoria.data.remote

import android.content.Context
import com.claudio.memoria.BuildConfig
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.security.MessageDigest

class UpdateRepository(
    private val context: Context,
    moshi: Moshi,
    private val okHttpClient: OkHttpClient
) {
    private val adapter = moshi.adapter(UpdateManifest::class.java)

    suspend fun fetchManifest(): UpdateManifest? = withContext(Dispatchers.IO) {
        runCatching {
            val request = Request.Builder()
                .url(BuildConfig.UPDATE_MANIFEST_URL)
                .header("Cache-Control", "no-cache")
                .get()
                .build()
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@use null
                val body = response.body?.string() ?: return@use null
                adapter.fromJson(body)
            }
        }.getOrNull()
    }

    suspend fun downloadApk(
        manifest: UpdateManifest,
        onProgress: (Float) -> Unit = {}
    ): File = withContext(Dispatchers.IO) {
        val dir = File(context.cacheDir, "updates").apply { mkdirs() }
        val target = File(dir, "memoria-${manifest.versionName}.apk")
        if (target.exists()) target.delete()

        val request = Request.Builder().url(manifest.apkUrl).get().build()
        okHttpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) error("HTTP ${response.code}")
            val body = response.body ?: error("Respuesta vacia")
            val total = body.contentLength()
            body.byteStream().use { input ->
                target.outputStream().use { output ->
                    val buffer = ByteArray(64 * 1024)
                    var read: Int
                    var downloaded = 0L
                    while (input.read(buffer).also { read = it } != -1) {
                        output.write(buffer, 0, read)
                        downloaded += read
                        if (total > 0) onProgress((downloaded.toFloat() / total).coerceIn(0f, 1f))
                    }
                }
            }
        }
        if (manifest.apkSizeBytes > 0 && target.length() != manifest.apkSizeBytes) {
            target.delete()
            error("Tamano inesperado")
        }
        target
    }

    fun sha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(64 * 1024)
            var read: Int
            while (input.read(buffer).also { read = it } != -1) {
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}
