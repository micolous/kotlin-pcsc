/*
 * Card.kt
 * Interface for SCARDHANDLE PC/SC operations
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

expect class Card {
    var protocol: Protocol? get

    // SCardDisconnect
    fun disconnect(disposition: DisconnectDisposition = DisconnectDisposition.Leave)

    // SCardReconnect
    fun reconnect(
        shareMode: ShareMode,
        preferredProtcols: Set<Protocol>?,
        initialization: Initialization
    )

    // SCardTransmit
    fun transmit(buffer: ByteArray) : ByteArray
}

fun Card.reconnect(shareMode: ShareMode, preferredProtocol: Protocol = Protocol.Any, initialization: Initialization)
        = reconnect(shareMode, setOf(preferredProtocol), initialization)
