/*
 * ReaderState.kt
 * Return value from Context.getStatusChange
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

/**
 * The state of the reader, as used in the `dwCurrentState` and `dwEventState` fields of the
 * [SCARD_READERSTATE struct](https://docs.microsoft.com/en-us/windows/win32/api/winscard/ns-winscard-scard_readerstatea).
 *
 * @see [ReaderState]
 */
data class State(
    val ignore: Boolean = false,
    val changed: Boolean = false,
    val unknown: Boolean = false,
    val unavailable: Boolean = false,
    val empty: Boolean = false,
    val present: Boolean = false,
    val atrMatch: Boolean = false,
    val exclusive: Boolean = false,
    val inUse: Boolean = false,
    val mute: Boolean = false,
    val unpowered: Boolean = false
) {
    val unaware = !(ignore || changed || unknown || unavailable || empty || present || atrMatch || exclusive || inUse || mute || unpowered)

    companion object {
        val UNAWARE = State()
    }
}

/**
 * The current state of the reader.
 *
 * Equivalent to [SCARD_READERSTATE](https://docs.microsoft.com/en-us/windows/win32/api/winscard/ns-winscard-scard_readerstatea).
 *
 * @see [Context.getStatus]
 * @see [Context.getStatusChange]
 */
data class ReaderState(
    val reader: String,
    val currentState: State = State.UNAWARE,
    val eventState: State = State.UNAWARE,
    val atr: ByteArray = ByteArray(0)
) {
    /**
     * Transforms this [ReaderState] into a version where the eventState is the current state.
     */
    fun update() = ReaderState(
        reader = reader,
        currentState = eventState,
        eventState = State.UNAWARE,
        atr = atr
    )
}
