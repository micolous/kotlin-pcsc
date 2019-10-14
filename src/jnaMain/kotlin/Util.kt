/*
 * Util.kt
 * Native utility functions for JNA interop
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

import java.nio.ByteBuffer

internal fun Collection<String>.asMultiString() : ByteArray {
    val buf = mutableListOf<Byte>()
    for (group in this) {
        group.toByteArray().toCollection(buf)

        // Null terminator for entry
        buf.add(0)
    }

    // Null terminator for list
    buf.add(0)

    return buf.toByteArray()
}
/*
internal fun Collection<String>.asMultiString() : ByteBuffer {
    // We're unlikely to have multi-byte characters as an input; but just in case, allow up to
    // 4 per character in the source string.
    val buf = ByteBuffer.allocate(map { (it.length * 4) + 1 }.sum() + 1)
    for (group in this) {
        buf.put(group.toByteArray())

        // Null terminator for entry
        buf.put(0)
    }

    // Null terminator for list
    buf.put(0)

    var finalLength = buf.position()
    return buf
}
 */

internal fun ByteBuffer.getMultiString(length: Int): Sequence<String> {
    val buffer = this
    val offset = position()
    return sequence {
        var start = 0

        while (buffer.hasRemaining() && (buffer.position() - offset) < length) {
            val index = buffer.position()
            if (buffer.get() == 0.toByte()) {
                // Terminator
                if (index == start) {
                    // final terminator
                    break
                }

                // Return the substring
                buffer.position(start)
                val arr = ByteArray(index - start)
                buffer.get(arr)
                yield(String(arr, 0, arr.size))

                // Move to next byte
                buffer.get()
                start = index + 1
            }
        }
    }
}
