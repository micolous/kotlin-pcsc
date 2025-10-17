/*
 * ReaderState.kt
 * Return value from Context.getStatusChange
 *
 * Copyright 2019-2025 Michael Farrell <micolous+git@gmail.com>
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
    /**
     * The reader should be ignored.
     */
    val ignore: Boolean = false,

    /**
     * The state of the reader has changed.
     */
    val changed: Boolean = false,

    /**
     * The reader name is unrecognised.
     */
    val unknown: Boolean = false,

    /**
     * The reader's state is unavailable.
     */
    val unavailable: Boolean = false,

    /**
     * There is no card in the reader.
     */
    val empty: Boolean = false,

    /**
     * There is a card in the reader.
     */
    val present: Boolean = false,

    /**
     * The ATR of the card in the reader matches one of the target cards.
     */
    val atrMatch: Boolean = false,

    /**
     * Another application is accessing the card in the reader exclusively.
     */
    val exclusive: Boolean = false,

    /**
     * Another application is accessing the card in the reader, but it may be opened in shared mode.
     */
    val inUse: Boolean = false,

    /**
     * The card in the reader is unresponsive.
     */
    val mute: Boolean = false,

    /**
     * The card in the reader has not been powered up.
     */
    val unpowered: Boolean = false
) {
    /**
     * The application is unaware of the current state, and would like to know.
     */
    val unaware = !(ignore || changed || unknown || unavailable || empty || present || atrMatch || exclusive || inUse || mute || unpowered)

    companion object {
        /**
         * The application is unaware of the current state, and would like to know.
         */
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
    /**
     * The name of the reader.
     */
    val reader: String,

    /**
     * The current state of the reader, as known by the application.
     */
    val currentState: State = State.UNAWARE,

    /**
     * The current state of the reader, as known by the smart card resource manager.
     */
    val eventState: State = State.UNAWARE,

    /**
     * The ATR of the inserted card.
     */
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
