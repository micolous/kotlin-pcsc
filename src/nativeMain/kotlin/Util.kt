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

import kotlin.collections.Collection
import kotlin.collections.MutableList
import kotlin.collections.forEachIndexed
import kotlin.sequences.Sequence
import kotlinx.cinterop.*

internal fun Collection<String>.asMultiString() : ByteArray {
    val buf = mutableListOf<Byte>()
    for (group in this) {
        group.encodeToByteArray(0, group.length, true).toCollection(buf)

        // Null terminator for entry
        buf.add(0)
    }

    // Null terminator for list
    buf.add(0)

    return buf.toByteArray()
}

internal fun ByteArray.toMultiString(): Sequence<String> {
    val array = this
    return sequence {
        var start = 0

        for (index in array.indices) {
            if (array[index] == 0.toByte()) {
                // terminator
                if (index == start) {
                    // final terminator
                    break
                }

                // Return the substring
                yield(array.decodeToString(start, index))
                start = index + 1
            }
        }
    }
}

inline fun <T : Any, R> T?.useNullablePinned(block: (Pinned<T>?) -> R): R {
    if (this == null) {
        return block(null)
    }

    return this.usePinned(block)
}
