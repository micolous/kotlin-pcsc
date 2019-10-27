/*
 * MultiStringTest.kt
 * Tests for MultiString
 *
 * Copyright 2019 Michael Farrell <micolous+git@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package au.id.micolous.kotlin.pcsc.test

import au.id.micolous.kotlin.pcsc.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@kotlin.ExperimentalUnsignedTypes
class MultiStringTest {
    /** Converts signed [Int] values into a [ByteArray] */
    private fun byteArrayOf(vararg i: Int) : ByteArray = i.map(Int::toByte).toByteArray()

    fun Byte.toHex() = toUByte().toString(16).padStart(2, '0').padEnd(3, ' ')

    /** Converts a [ByteArray] to a space-separated hex string for display */
    fun ByteArray.toHex() = buildHex(map { it.toHex() })

    fun Iterable<Byte>.toHex() = buildHex(map { it.toHex() })

    private fun buildHex(it: Iterable<String>) = it.fold(
        StringBuilder(), StringBuilder::append).dropLast(1).toString()

    private val helloWorld = byteArrayOf(
        0x68, 0x65, 0x6c, 0x6c, 0x6f, 0, // hello\0
        0x77, 0x6f, 0x72, 0x6c, 0x64, 0, // world\0
        0
    )

    private val singleString = byteArrayOf(
        0x68, 0x65, 0x6c, 0x6c, 0x6f, 0 // hello\0
        // Intentionally missing extra null terminator
    )

    @Test
    fun testHelloWorld() {
        val b = helloWorld.toMultiString().toList()
        assertEquals(listOf("hello", "world"), b)

        val e = b.asMultiString()
        assertEquals(helloWorld.toHex(), e.toHex())
    }

    @Test
    fun testOffset() {
        val b = helloWorld.toMultiString(off = 6).toList()
        assertEquals(listOf("world"), b)

        val e = b.asMultiString()
        assertEquals(helloWorld.slice(6 until helloWorld.size).toHex(), e.toHex())
    }

    @Test
    fun testSingleString() {
        val b = singleString.toMultiString().toList()
        assertEquals(listOf("hello"), b)

        // The result should change (two null terminators)
        val e = b.asMultiString()
        assertEquals((singleString + byteArrayOf(0)).toHex(), e.toHex())
    }

    @Test
    fun testUnterminated() {
        val b = helloWorld.toMultiString(off = 6, len = 5).toList()
        assertEquals(listOf("world"), b)
    }

    @Test
    fun testUnterminatedMulti() {
        val b = helloWorld.toMultiString(off = 3, len = 5).toList()
        assertEquals(listOf("lo", "wo"), b)
    }
}