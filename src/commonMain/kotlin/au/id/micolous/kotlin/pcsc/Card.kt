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

/**
 * Interface for all smartcard operations (`SCARDHANDLE`).
 *
 * @see Context.connect
 */
expect class Card {
    /**
     * The [Protocol] in use for communication with this [Card], or `null` if unknown.
     *
     * This is equivalent to the `pdwActiveProtocol` return value from `SCardConnect`
     * and `SCardReconnect`.
     *
     * @see [Context.connect]
     * @see [reconnect]
     */
    var protocol: Protocol? get

    /**
     * Disconnects from the given card.
     *
     * Equivalent to `SCardDisconnect`.
     *
     * @param disposition State to leave the card in when disconnecting.
     * Defaults to doing nothing ([DisconnectDisposition.Leave]).
     */
    fun disconnect(disposition: DisconnectDisposition = DisconnectDisposition.Leave)

    /**
     * Disconnects and reconnects to the given card with new parameters.
     *
     * The new protocol in use will be available as [protocol].
     *
     * Equivalent to `SCardReconnect`.
     *
     * @param initialization An [Initialization] procedure to perform when
     * disconnecting from the card.
     *
     * @see [Context.connect]
     */
    fun reconnect(
        shareMode: ShareMode,
        preferredProtcols: Set<Protocol>?,
        initialization: Initialization
    )

    /**
     * Transmits an APDU to the card, and returns the response.
     *
     * Equivalent to `SCardTransmit`. `pioSendPci` is automatically populated
     * based on [protocol].
     *
     * @param buffer APDU bytes to send to the card.
     * @returns APDU bytes received from the card.
     * @throws NotImplementedError if [protocol] is not supported
     * @throws PCSCError
     */
    fun transmit(buffer: ByteArray) : ByteArray

    /**
     * Starts a new transaction on the card.
     *
     * Equivalent to `SCardBeginTransaction`.
     * @throws PCSCError
     */
    fun beginTransaction()

    /**
     * Ends a previously declared transaction.
     *
     * Equivalent to `SCardEndTransaction`.
     *
     * @param disposition State to leave the card in when disconnecting.
     * Defaults to doing nothing ([DisconnectDisposition.Leave]).
     * @throws PCSCError
     */
    fun endTransaction(disposition: DisconnectDisposition = DisconnectDisposition.Leave)

    /**
     * Gets the current status of a smart card in this reader.
     *
     * Equivalent to `SCardStatus`.
     *
     * @throws PCSCError
     */
    fun status(): CardStatus
}

/**
 * Disconnects and reconnects to the given card with new parameters.
 *
 * @param preferredProtocol Single preferred protocol to use.
 * If not specified, defaults to [Protocol.Any].
 *
 * @see [Card.reconnect]
 */
fun Card.reconnect(shareMode: ShareMode, preferredProtocol: Protocol = Protocol.Any, initialization: Initialization)
        = reconnect(shareMode, setOf(preferredProtocol), initialization)
