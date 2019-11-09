/*
 * Util.kt
 * Native utility functions for Kotlin/Native C/Interop
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
package au.id.micolous.kotlin.pcsc

import au.id.micolous.kotlin.pcsc.internal.*
import kotlin.reflect.*
import kotlinx.cinterop.*

/**
 * Similar to [usePinned], but handles null values.
 *
 * If [this] is `null`, then [block] will be executed with a parameter of `null`.
 *
 * If [this] is non-`null`, then [block] will be executed with a [Pinned] version of [this].
 */
internal inline fun <T : Any, R> T?.useNullablePinned(block: (Pinned<T>?) -> R): R {
    if (this == null) {
        return block(null)
    }

    return this.usePinned(block)
}

/**
 * Creates a new [ByteArray], or returns `null` if [length] is 0.
 */
internal fun maybeByteArray(length: Int): ByteArray? {
    require(length >= 0) { "buffer length must be at least 0" }
    return when (length) {
        0 -> null
        else -> ByteArray(length)
    }
}

internal inline fun maybeByteArray(length: DWORD) = maybeByteArray(length.toInt())
internal inline fun maybeByteArray(length: DWORDVar) = maybeByteArray(length.value.toInt())

/**
 * Creates a new [UByteArray], or returns `null` if [length] is 0.
 */
internal fun maybeUByteArray(length: Int): UByteArray? {
    require(length >= 0) { "buffer length must be at least 0" }
    return when (length) {
        0 -> null
        else -> UByteArray(length)
    }
}

internal inline fun maybeUByteArray(length: DWORD) = maybeUByteArray(length.toInt())
internal inline fun maybeUByteArray(length: DWORDVar) = maybeUByteArray(length.value.toInt())
