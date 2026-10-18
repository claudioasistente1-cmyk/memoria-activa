package com.claudio.memoria.domain.model

enum class GameType(
    val id: String,
    val title: String,
    val emoji: String,
    val description: String
) {
    PAIRS("pairs", "Encontrar parejas", "\uD83C\uDFB4", "Encontrá las cartas iguales"),
    SEQUENCE("sequence", "Recordar secuencias", "\uD83D\uDD35", "Repetí el orden de los colores"),
    VISUAL_MEMORY("visual_memory", "Memoria visual", "\uD83D\uDC41", "Recordá dónde estaban las figuras"),
    MATH("math", "Cálculo mental", "\uD83D\uDD22", "Resolvé cuentas sencillas"),
    LOGIC("logic", "Patrones y lógica", "\uD83E\uDDE9", "Descubrí qué sigue en la serie");

    companion object {
        fun fromId(id: String): GameType? = values().firstOrNull { it.id == id }
    }
}
