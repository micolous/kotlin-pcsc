/*
 * CardStatus.kt
 * Informational structure for Card.status
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

data class CardStatus internal constructor(
    /**
     * Name(s) of the reader. On Windows, this may be multiple names.
     */
    val readerNames: List<String> = emptyList(),
    // On pcsclite (Linux / macOS), these are a bitmask
    // On Windows, only one of these is true
    // https://pcsclite.apdu.fr/api/group__API.html#differences
    val unknown: Boolean = false,
    val absent: Boolean = false,
    val present: Boolean = false,
    val swallowed: Boolean = false,
    val powered: Boolean = false,
    val negotiable: Boolean = false,
    val specific: Boolean = false,
    val protocol: Protocol? = Protocol.Undefined,
    val atr: ByteArray = ByteArray(0)
)