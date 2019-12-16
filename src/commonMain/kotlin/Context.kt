/*
 * Context.kt
 * Interface for SCARDCONTEXT PC/SC operations
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

import kotlinx.coroutines.*

/**
 * Main interface for PC/SC API operations (`SCARDCONTEXT`).
 *
 * @see Context.establish
 */
expect class Context {
    /**
     * Releases the given context, and renders it unusable.
     *
     * Equivalent to `SCardReleaseContext`.
     */
    fun release()

    /**
     * Returns `true` if this class represents a valid context.
     *
     * Equivalent to `SCardIsValidContext`.
     */
    fun isValid() : Boolean

    /**
     * Terminates all outstanding actions within this [Context].
     *
     * Equivalent to `SCardCancel`.
     *
     * @throws PCSCError
     */
    fun cancel()

    /**
     * Lists all readers available in this [Context].
     *
     * Equivalent to `SCardListReaders`.
     *
     * @param groups If specified, a list of group names to search within.
     * @return [List] of reader names, as [String], which can be used with [connect].
     * If there are no readers available, returns an empty list.
     */
    fun listReaders(groups: List<String>? = null) : List<String>

    /**
     * Connects to a given reader.
     *
     * Equivalent to `SCardConnect`.
     *
     * @param reader Reader name from [listReaders]
     * @param shareMode Flag to indicate if other applications may form connections to the card
     * @param preferredProtcols A set of protocols to use when communicating.
     * If `null`, then no protocol negotiation will take place.
     * @return [Card] object to communicate with the card. The protocol in use is
     * avaliable as [Card.protocol]
     * @throws PCSCError
     */
    fun connect(reader: String, shareMode: ShareMode, preferredProtcols: Set<Protocol>?) : Card

    /**
     * Waits for status changes in the given list of readers.
     *
     * Equivalent to `SCardGetStatusChange`.
     *
     * @param timeout Number of seconds to wait. To wait for a long time, use [LONG_TIMEOUT].
     */
    suspend fun getStatusChange(timeout: Int, readers: List<ReaderState>) : List<ReaderState>

    companion object {
        /**
         * Establishes a new PC/SC context.
         *
         * Equivalent to `SCardEstablishContext`.
         *
         * @param scope Scope to use. Defaults to [Scope.User]
         * @return [Context] for working with PC/SC API
         * @throws PCSCError
         */
        fun establish(scope: Scope = Scope.User) : Context
    }
}

/**
 * @see [Context.connect]
 * @param preferredProtocol Single preferred protocol to use.
 * If not specified, defaults to [Protocol.Any].
 */
fun Context.connect(reader: String, shareMode: ShareMode, preferredProtocol: Protocol = Protocol.Any) : Card
        = connect(reader, shareMode, setOf(preferredProtocol))

suspend fun Context.getStatusChange(timeout: Int, reader: ReaderState)
    = getStatusChange(timeout, listOf(reader)).first()

/**
 * Gets the current state of the given readers, and returns immediately.
 *
 * This is equivalent to `SCardGetStatusChange(dwTimeout = 0, ...)`.
 *
 * @see [Context.getStatusChange]
 */
fun Context.getStatus(readers: List<String>) : List<ReaderState> = runBlocking {
    getStatusChange(0, readers.map { ReaderState(it) })
}

fun Context.getStatus(reader: String): ReaderState
        = getStatus(listOf(reader)).first()

/**
 * Gets the status of all connected readers.
 *
 * @param groups If specified, a list of group names to search within.
 * @returns [List] of all connected readers
 * @see [Context.getStatus], [Context.listReaders]
 */
fun Context.getAllReaderStatus(groups: List<String>? = null): List<ReaderState>
        = listReaders(groups).takeUnless { it.isEmpty() }?.let(::getStatus).orEmpty()
