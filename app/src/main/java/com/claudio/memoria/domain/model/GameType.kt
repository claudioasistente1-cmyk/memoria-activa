package com.claudio.memoria.domain.model

enum class GameType(
    val id: String,
    val title: String,
    val emoji: String,
    val description: String
) {
    PAIRS("pairs", "Encontrar parejas", "\uD83C\uDFB4", "Encontra las cartas iguales"),
    SEQUENCE("sequence", "Recordar secuencias", "\uD83D\uDD35", "Repeti el orden de los colores"),
    VISUAL_MEMORY("visual_memory", "Memoria visual", "\uD83D\uDC41", "Recorda donde estaban las figuras"),
    MATH("math", "Calculo mental", "\uD83D\uDD22", "Resolve cuentas sencillas"),
    LOGIC("logic", "Patrones y logica", "\uD83E\uDDE9", "Descubri que sigue en la serie");

    companion object {
        fun fromId(id: String): GameType? = values().firstOrNull { it.id == id }
    }
}
