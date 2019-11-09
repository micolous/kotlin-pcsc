/*
 * SCardReaderState.kt
 * SCARD_READERSTATE
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

import au.id.micolous.kotlin.pcsc.*
import com.sun.jna.Platform
import com.sun.jna.Pointer
import com.sun.jna.Structure

private val READER_STATE_ALIGN = if (Platform.isMac()) {
    Structure.ALIGN_NONE
} else {
    Structure.ALIGN_DEFAULT
}

private const val SCARD_STATE_UNAWARE = 0x0000L
private const val SCARD_STATE_IGNORE = 0x0001L
private const val SCARD_STATE_CHANGED = 0x0002L
private const val SCARD_STATE_UNKNOWN = 0x0004L
private const val SCARD_STATE_UNAVAILABLE = 0x0008L
private const val SCARD_STATE_EMPTY = 0x0010L
private const val SCARD_STATE_PRESENT = 0x0020L
private const val SCARD_STATE_ATRMATCH = 0x0040L
private const val SCARD_STATE_EXCLUSIVE = 0x0080L
private const val SCARD_STATE_INUSE = 0x0100L
private const val SCARD_STATE_MUTE = 0x0200L
private const val SCARD_STATE_UNPOWERED = 0x0400L


internal class SCardReaderState(p: Pointer? = null) : Structure(p, READER_STATE_ALIGN) {
    @JvmField
    var szReader = ""
    @JvmField
    var pvUserData: Pointer? = null
    @JvmField
    var dwCurrentState = Dword()
    @JvmField
    var dwEventState = Dword()
    @JvmField
    var cbAtr = Dword()
    @JvmField
    var rgbAtr = ByteArray(MAX_ATR_SIZE)

    override fun getFieldOrder(): MutableList<String> {
        return mutableListOf(
            "szReader",
            "pvUserData",
            "dwCurrentState",
            "dwEventState",
            "cbAtr",
            "rgbAtr"
        )
    }

    override fun toString(): String {
        return "${javaClass.name}{szReader: $szReader, pvUserData: $pvUserData, " +
                "dwCurrentState: $dwCurrentState, dwEventState: $dwEventState, cbAtr: $cbAtr, " +
                "rgbAtr: $rgbAtr"
    }

    internal fun unwrap() = ReaderState(
        reader = szReader,
        currentState = dwCurrentState.toState(),
        eventState = dwEventState.toState(),
        atr = rgbAtr.copyOf(cbAtr.toInt())
    )

    internal fun copyFrom(state: ReaderState) {
        szReader = state.reader
        dwCurrentState = state.currentState.toDword()
        dwEventState = state.eventState.toDword()
        state.atr.copyInto(rgbAtr)
        cbAtr = Dword(state.atr.size.toLong())
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        internal fun makeArray(size: Int): Array<SCardReaderState> {
            return SCardReaderState().toArray(size)!! as Array<SCardReaderState>
        }
    }
}

private fun State.toDword() = Dword(
        if (ignore) SCARD_STATE_IGNORE else 0L or
        if (changed) SCARD_STATE_CHANGED else 0L or
        if (unknown) SCARD_STATE_UNKNOWN else 0L or
        if (unavailable) SCARD_STATE_UNAVAILABLE else 0L or
        if (empty) SCARD_STATE_EMPTY else 0L or
        if (present) SCARD_STATE_PRESENT else 0L or
        if (atrMatch) SCARD_STATE_ATRMATCH else 0L or
        if (exclusive) SCARD_STATE_EXCLUSIVE else 0L or
        if (inUse) SCARD_STATE_INUSE else 0L or
        if (mute) SCARD_STATE_MUTE else 0L or
        if (unpowered) SCARD_STATE_UNPOWERED else 0L
    )

private fun Dword.toState(): State {
    val v = toLong()
    return State(
        ignore = v.hasBits(SCARD_STATE_IGNORE),
        changed = v.hasBits(SCARD_STATE_CHANGED),
        unknown = v.hasBits(SCARD_STATE_UNKNOWN),
        unavailable = v.hasBits(SCARD_STATE_UNAVAILABLE),
        empty = v.hasBits(SCARD_STATE_EMPTY),
        present = v.hasBits(SCARD_STATE_PRESENT),
        atrMatch = v.hasBits(SCARD_STATE_ATRMATCH),
        exclusive = v.hasBits(SCARD_STATE_EXCLUSIVE),
        inUse = v.hasBits(SCARD_STATE_INUSE),
        mute = v.hasBits(SCARD_STATE_MUTE),
        unpowered = v.hasBits(SCARD_STATE_UNPOWERED)
    )
}

internal fun ReaderState.toJna() = SCardReaderState().apply {
    copyFrom(this@toJna)
}
