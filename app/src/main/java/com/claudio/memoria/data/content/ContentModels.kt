package com.claudio.memoria.data.content

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GameContent(
    @Json(name = "schemaVersion") val schemaVersion: Int = 1,
    @Json(name = "contentVersion") val contentVersion: Int = 1,
    @Json(name = "minAppVersionCode") val minAppVersionCode: Int = 1,
    @Json(name = "pairs") val pairs: PairsContent = PairsContent(),
    @Json(name = "visualMemory") val visualMemory: VisualMemoryContent = VisualMemoryContent(),
    @Json(name = "sequence") val sequence: SequenceContent = SequenceContent(),
    @Json(name = "math") val math: List<MathLevelContent> = emptyList(),
    @Json(name = "series") val series: List<SeriesLevelContent> = emptyList(),
    @Json(name = "texts") val texts: TextsContent = TextsContent()
) {
    companion object {
        const val SUPPORTED_SCHEMA = 1
    }
}

@JsonClass(generateAdapter = true)
data class PairsContent(
    @Json(name = "symbols") val symbols: List<String> = DEFAULT_SYMBOLS
)

@JsonClass(generateAdapter = true)
data class VisualMemoryContent(
    @Json(name = "symbols") val symbols: List<String> = DEFAULT_VISUAL_SYMBOLS
)

@JsonClass(generateAdapter = true)
data class SequenceContent(
    @Json(name = "colors") val colors: List<String> = DEFAULT_COLORS,
    @Json(name = "paletteSize") val paletteSize: Int = 4
)

@JsonClass(generateAdapter = true)
data class MathLevelContent(
    @Json(name = "level") val level: Int = 1,
    @Json(name = "questions") val questions: List<MathItemContent> = emptyList()
)

@JsonClass(generateAdapter = true)
data class MathItemContent(
    @Json(name = "text") val text: String = "",
    @Json(name = "answer") val answer: Int = 0,
    @Json(name = "options") val options: List<Int> = emptyList()
)

@JsonClass(generateAdapter = true)
data class SeriesLevelContent(
    @Json(name = "level") val level: Int = 1,
    @Json(name = "items") val items: List<SeriesItemContent> = emptyList()
)

@JsonClass(generateAdapter = true)
data class SeriesItemContent(
    @Json(name = "sequence") val sequence: List<Int> = emptyList(),
    @Json(name = "answer") val answer: Int = 0,
    @Json(name = "options") val options: List<Int> = emptyList()
)

@JsonClass(generateAdapter = true)
data class TextsContent(
    @Json(name = "homeGreeting") val homeGreeting: String = "Hola",
    @Json(name = "dailyIntro") val dailyIntro: String = "Tus ejercicios de hoy",
    @Json(name = "encouragement") val encouragement: String = "Cada dia entrenamos la memoria juntos."
)

val DEFAULT_SYMBOLS = listOf(
    "\uD83C\uDF4E", "\uD83C\uDF4C", "\uD83C\uDF53", "\uD83C\uDF47", "\uD83C\uDF52",
    "\uD83D\uDD14", "\u2B50", "\uD83C\uDF3B", "\uD83C\uDF40", "\uD83C\uDF49",
    "\uD83D\uDC26", "\uD83D\uDC1D", "\uD83D\uDE82", "\u26BD", "\uD83C\uDFB5"
)

val DEFAULT_VISUAL_SYMBOLS = listOf(
    "\uD83C\uDF4E", "\uD83C\uDF4C", "\uD83C\uDF3B", "\uD83D\uDD14",
    "\u2B50", "\uD83C\uDF40", "\uD83D\uDC26", "\uD83C\uDFB5"
)

val DEFAULT_COLORS = listOf(
    "#E05C5C", "#4C8DD9", "#58B368", "#F2B441"
)
