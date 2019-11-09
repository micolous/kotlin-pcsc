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

// Equivalent to SCARD_STATE_*
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

data class ReaderState(
    val reader: String,
    val currentState: State = State.UNAWARE,
    val eventState: State = State.UNAWARE,
    val atr: ByteArray = ByteArray(0)
)
