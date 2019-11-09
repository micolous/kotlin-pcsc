/*
 * Common.kt
 * Interface for common definitions
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

/** If the host byte order is little endian, this returns true. */
expect internal val isLittleEndian: Boolean

internal fun ByteArray.asBigEndian(): ByteArray = if (isLittleEndian) reversedArray() else this
internal fun ByteArray.getLong(off: Int = 0, len: Int = off - size): Long {
    require(off >= 0) { "off must be >= 0" }
    require(len >= 0) { "len must be >= 0" }
    val last = off + len
    require(last <= size) { "length must be less than ${size - off}"}

    var o = 0L
    for (i in (off until last)) {
        o = (o shl 8) or (get(i).toLong() and 0xffL)
    }

    return o
}

internal fun ByteArray.getInt(off: Int = 0, len: Int = off - size): Int = getLong(off, len).toInt()

/** A really long timeout value. */
const val LONG_TIMEOUT = 50 * 86400 // days

internal inline fun Int.hasBits(mask: Int) = (this and mask) == mask
internal inline fun Long.hasBits(mask: Long) = (this and mask) == mask
internal inline fun UInt.hasBits(mask: UInt) = (this and mask) == mask
