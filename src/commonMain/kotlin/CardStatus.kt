/*
 * CardStatus.kt
 * Informational structure for Card.status
 *
 * Copyright 2019-2021 Michael Farrell <micolous+git@gmail.com>
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
 * Return value from [Card.status] (`SCardStatus`).
 *
 * The status [Boolean]s ([unknown], [absent], [present], [swallowed], [powered], [negotiable] and
 * [specific]) have platform-specific differences:
 *
 * * On `pcsclite` (Linux and macOS), these are a bitmask.
 * * On Windows, only **one** of these will be true, as it represents the lifecycle of the card.
 *
 * For example, when a card is connected and the protocol has been negotiated:
 *
 * * `pcsclite` would have [present], [powered] and [specific] == `true`
 * * Windows would have [specific] == `true`, but [present] and [powered] == `false`
 *
 * See [pcsclite's API differences](https://pcsclite.apdu.fr/api/group__API.html#differences) for
 * more details.
 */
data class CardStatus internal constructor(
    /**
     * Name(s) of the reader. On Windows, this may be multiple names.
     */
    val readerNames: List<String> = emptyList(),

    /**
     * If true, the reader / card is in an unknown state.
     */
    val unknown: Boolean = false,
    /**
     * If true, there is no card in the reader.
     */
    val absent: Boolean = false,
    /**
     * If true, there is a card in the reader.
     *
     * On Windows, it has not been moved in position for use.
     */
    val present: Boolean = false,
    /**
     * If true, there is a card in the reader in position for use, but the card is not powered.
     */
    val swallowed: Boolean = false,
    /**
     * If true, power is being provided to the card.
     *
     * On Windows, the reader driver is also unaware of the mode of the card.
     */
    val powered: Boolean = false,
    /**
     * If true, the card has been reset and is awaiting PTS negotation.
     */
    val negotiable: Boolean = false,
    /**
     * If true, the card has been reset and specific communication protocols have been established.
     */
    val specific: Boolean = false,

    /**
     * The current protocol, if any.
     *
     * This only has meaning if [specific] is true.
     */
    val protocol: Protocol? = Protocol.Undefined,
    /**
     * The ATR (Answer To Reset) returned by the card, if available.
     */
    val atr: ByteArray = ByteArray(0)
) {
    /**
     * Name of the reader.
     *
     * Note: Windows may have many names for the same reader -- this simply returns the first.
     */
    val readerName = readerNames.firstOrNull()
}
