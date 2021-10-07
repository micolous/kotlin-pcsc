/*
 * Types.kt
 * Platform-specific types used for JNA
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
package au.id.micolous.kotlin.pcsc.jna

import com.sun.jna.IntegerType
import com.sun.jna.NativeLong
import com.sun.jna.Platform
import com.sun.jna.Pointer
import com.sun.jna.ptr.ByReference


val DWORD_SIZE = if (Platform.isWindows() || Platform.isMac()) { 4 } else { NativeLong.SIZE }
val HANDLE_SIZE = if (Platform.isWindows()) { Pointer.SIZE } else { DWORD_SIZE }

internal open class KIntegerType(size: Int, value: Long = 0, unsigned: Boolean = true) : IntegerType(size, value, unsigned) {
    // This is needed to fill in the gaps of IntegerType's implementation of Number for Kotlin.
    override fun toByte() = toInt().toByte()
    override fun toChar() = toInt().toChar()
    override fun toShort() = toInt().toShort()
}

internal class Dword(value: Long = 0) : KIntegerType(DWORD_SIZE, value) {
    constructor() : this(0)
}

internal class DwordByReference() : ByReference(DWORD_SIZE) {
    constructor(value: Dword) : this() {
        this.value = value
    }

    init {
        internalSetValue()
    }

    var value: Dword
        get() = Dword(when (DWORD_SIZE) {
            4 -> pointer.getInt(0).toLong() and 0xffffffffL
            8 -> pointer.getLong(0)
            else -> throw NotImplementedError("DWORD_SIZE = $DWORD_SIZE")
        })

        set(value) = internalSetValue(value)

    private fun internalSetValue(value: Dword? = null) {
        when (DWORD_SIZE) {
            4 -> pointer.setInt(0, value?.toInt() ?: 0)
            8 -> pointer.setLong(0, value?.toLong() ?: 0)
            else -> throw NotImplementedError("DWORD_SIZE = $DWORD_SIZE")
        }
    }
}

internal class Handle(value: Long = 0) : KIntegerType(HANDLE_SIZE, value)

internal class HandleByReference : ByReference(HANDLE_SIZE) {
    var value: Long
        get() = when (HANDLE_SIZE) {
            4 -> pointer.getInt(0).toLong()
            8 -> pointer.getLong(0)
            else -> throw NotImplementedError("HANDLE_SIZE = $HANDLE_SIZE")
        }

        set(value) = when (HANDLE_SIZE) {
            4 -> pointer.setInt(0, value.toInt())
            8 -> pointer.setLong(0, value)
            else -> throw NotImplementedError("HANDLE_SIZE = $HANDLE_SIZE")
        }

    val handle: Handle get() { return Handle(value) }
}

internal typealias SCardResult = Dword
internal typealias SCardContext = Handle
internal typealias SCardContextByReference = HandleByReference
internal typealias SCardHandle = Handle
internal typealias SCardHandleByReference = HandleByReference
