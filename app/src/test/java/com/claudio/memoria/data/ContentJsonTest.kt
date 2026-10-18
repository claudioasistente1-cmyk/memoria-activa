package com.claudio.memoria.data

import com.claudio.memoria.data.content.GameContent
import com.claudio.memoria.data.remote.UpdateManifest
import com.squareup.moshi.Moshi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ContentJsonTest {

    private val moshi = Moshi.Builder().build()

    @Test
    fun `game content uses defaults when optional fields are missing`() {
        val adapter = moshi.adapter(GameContent::class.java)
        val content = adapter.fromJson("""{"schemaVersion":1,"contentVersion":2}""")
        assertEquals(2, content?.contentVersion)
        assertTrue(content!!.pairs.symbols.isNotEmpty())
        assertTrue(content.visualMemory.symbols.isNotEmpty())
        assertEquals(4, content.sequence.paletteSize)
    }

    @Test
    fun `game content parses math and series`() {
        val adapter = moshi.adapter(GameContent::class.java)
        val json = """
            {
              "schemaVersion": 1,
              "contentVersion": 5,
              "math": [{ "level": 1, "questions": [{ "text": "2 + 2", "answer": 4 }] }],
              "series": [{ "level": 2, "items": [{ "sequence": [1,2,3], "answer": 4 }] }]
            }
        """.trimIndent()
        val content = adapter.fromJson(json)!!
        assertEquals("2 + 2", content.math.first().questions.first().text)
        assertEquals(4, content.math.first().questions.first().answer)
        assertEquals(4, content.series.first().items.first().answer)
    }

    @Test
    fun `update manifest requires https url and a sha256 hash`() {
        val valid = UpdateManifest(
            versionCode = 10,
            versionName = "1.1.0",
            apkUrl = "https://example.com/app.apk",
            sha256 = "a".repeat(64)
        )
        assertTrue(valid.isUsable())

        assertFalse(valid.copy(apkUrl = "http://example.com/app.apk").isUsable())
        assertFalse(valid.copy(sha256 = "abc").isUsable())
        assertFalse(valid.copy(versionCode = 0).isUsable())
    }
}
