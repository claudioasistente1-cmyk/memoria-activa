package com.claudio.memoria.data.content

import android.content.Context
import com.claudio.memoria.BuildConfig
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Owns the game content. Starts with the bundled asset, upgrades to the last valid
 * cached copy, and (when online) tries to fetch a newer valid copy. Downloaded content
 * is data only: it is validated and never executed.
 */
class ContentRepository(
    private val context: Context,
    moshi: Moshi,
    private val okHttpClient: OkHttpClient
) {
    private val adapter = moshi.adapter(GameContent::class.java)
    private val cacheFile = File(context.filesDir, "content_cached.json")
    private val appVersionCode = BuildConfig.VERSION_CODE

    private val _content = MutableStateFlow(loadInitial())
    val content: StateFlow<GameContent> = _content.asStateFlow()

    private fun readAsset(): GameContent? = runCatching {
        context.assets.open("content.json").bufferedReader().use { it.readText() }
            .let { adapter.fromJson(it) }
    }.getOrNull()

    private fun loadInitial(): GameContent {
        val bundled = readAsset()?.takeIf(::isValid) ?: GameContent()
        val cached = runCatching {
            if (cacheFile.exists()) adapter.fromJson(cacheFile.readText()) else null
        }.getOrNull()?.takeIf(::isValid)
        return if (cached != null && cached.contentVersion >= bundled.contentVersion) cached else bundled
    }

    private fun isValid(content: GameContent): Boolean {
        if (content.schemaVersion != GameContent.SUPPORTED_SCHEMA) return false
        if (content.minAppVersionCode > appVersionCode) return false
        if (content.math.any { it.level < 1 }) return false
        if (content.series.any { it.level < 1 }) return false
        return content.pairs.symbols.isNotEmpty() && content.visualMemory.symbols.isNotEmpty()
    }

    /** Returns true when a newer content version was applied. */
    suspend fun refresh(): Boolean = withContext(Dispatchers.IO) {
        val current = _content.value
        val request = Request.Builder()
            .url(BuildConfig.CONTENT_URL)
            .header("Cache-Control", "no-cache")
            .get()
            .build()
        runCatching {
            okHttpClient.newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()
                .newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@use false
                    val body = response.body?.string() ?: return@use false
                    val parsed = adapter.fromJson(body) ?: return@use false
                    if (!isValid(parsed) || parsed.contentVersion <= current.contentVersion) {
                        return@use false
                    }
                    cacheFile.writeText(body)
                    _content.value = parsed
                    true
                }
        }.getOrDefault(false)
    }

    fun current(): GameContent = _content.value
}
