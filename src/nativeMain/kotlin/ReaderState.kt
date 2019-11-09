/*
 * ReaderState.kt
 * Native implementation of PC/SC SCARD_READERSTATE
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
import kotlinx.cinterop.*
import platform.posix.*


private fun State.toDword() = (
    if (ignore) SCARD_STATE_IGNORE.convert<DWORD>() else DWORD_ZERO or
    if (changed) SCARD_STATE_CHANGED.convert<DWORD>() else DWORD_ZERO or
    if (unknown) SCARD_STATE_UNKNOWN.convert<DWORD>() else DWORD_ZERO or
    if (unavailable) SCARD_STATE_UNAVAILABLE.convert<DWORD>() else DWORD_ZERO or
    if (empty) SCARD_STATE_EMPTY.convert<DWORD>() else DWORD_ZERO or
    if (present) SCARD_STATE_PRESENT.convert<DWORD>() else DWORD_ZERO or
    if (atrMatch) SCARD_STATE_ATRMATCH.convert<DWORD>() else DWORD_ZERO or
    if (exclusive) SCARD_STATE_EXCLUSIVE.convert<DWORD>() else DWORD_ZERO or
    if (inUse) SCARD_STATE_INUSE.convert<DWORD>() else DWORD_ZERO or
    if (mute) SCARD_STATE_MUTE.convert<DWORD>() else DWORD_ZERO or
    if (unpowered) SCARD_STATE_UNPOWERED.convert<DWORD>() else DWORD_ZERO
)

private fun DWORD.toState(): State {
    return State(
        ignore = hasBits(SCARD_STATE_IGNORE.convert<DWORD>()),
        changed = hasBits(SCARD_STATE_CHANGED.convert<DWORD>()),
        unknown = hasBits(SCARD_STATE_UNKNOWN.convert<DWORD>()),
        unavailable = hasBits(SCARD_STATE_UNAVAILABLE.convert<DWORD>()),
        empty = hasBits(SCARD_STATE_EMPTY.convert<DWORD>()),
        present = hasBits(SCARD_STATE_PRESENT.convert<DWORD>()),
        atrMatch = hasBits(SCARD_STATE_ATRMATCH.convert<DWORD>()),
        exclusive = hasBits(SCARD_STATE_EXCLUSIVE.convert<DWORD>()),
        inUse = hasBits(SCARD_STATE_INUSE.convert<DWORD>()),
        mute = hasBits(SCARD_STATE_MUTE.convert<DWORD>()),
        unpowered = hasBits(SCARD_STATE_UNPOWERED.convert<DWORD>())
    )
}

internal fun SCARD_READERSTATE.copyFrom(memScope: MemScope, state: ReaderState) {
    szReader = state.reader.cstr.getPointer(memScope)
    dwCurrentState = state.currentState.toDword()
    dwEventState = state.eventState.toDword()


    state.atr.toUByteArray().forEachIndexed { i, b -> rgbAtr.set(i, b) }
    cbAtr = state.atr.size.convert<DWORD>()
}

internal fun SCARD_READERSTATE.unwrap() = ReaderState(
    reader = szReader?.toKString() ?: "",
    currentState = dwCurrentState.toState(),
    eventState = dwEventState.toState(),
    atr = rgbAtr.readBytes(cbAtr.toInt())
)