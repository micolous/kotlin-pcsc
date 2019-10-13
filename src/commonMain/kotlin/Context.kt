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

expect class Context {
    // SCardReleaseContext
    fun release()

    // SCardIsValidContext
    fun isValid() : Boolean

    // SCardListReaders
    fun listReaders(groups: List<String>? = null) : List<String>

    // SCardConnect
    fun connect(reader: String, shareMode: ShareMode, preferredProtcols: Set<Protocol>?) : Card

    companion object {
        // SCardEstablishContext
        fun establish(scope: Scope = Scope.User) : Context
    }
}

fun Context.connect(reader: String, shareMode: ShareMode, preferredProtocol: Protocol = Protocol.Any) : Card
        = connect(reader, shareMode, setOf(preferredProtocol))

