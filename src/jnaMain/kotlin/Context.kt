/*
 * Context.kt
 * JNA implementation of SCARDCONTEXT PC/SC operations
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

import au.id.micolous.kotlin.pcsc.jna.*
import java.nio.ByteBuffer

actual class Context private constructor(private var handle: SCardContext?) {

    // SCardReleaseContext
    actual fun release() {
        val handle = handle ?: return
        wrapPCSCErrors {
            LIB.value.SCardReleaseContext(handle)
        }
        this.handle = null
    }

    // SCardIsValidContext
    actual fun isValid() : Boolean {
        return when (val handle = handle) {
            null -> false
            else -> wrapPCSCErrors(falseValue = PCSCErrorCode.E_INVALID_HANDLE) {
                LIB.value.SCardIsValidContext(handle)
            }
        }
    }

    // SCardCancel
    actual fun cancel() {
        val handle = nonNullHandle()
        wrapPCSCErrors {
            LIB.value.SCardCancel(handle)
        }
    }

    // SCardListReaders
    actual fun listReaders(groups: List<String>?) : List<String> {
        val handle = nonNullHandle()
        val pcchReaders = DwordByReference()
        val mszGroups = groups?.asMultiString()

        val hasReaders = wrapPCSCErrors(falseValue = PCSCErrorCode.E_NO_READERS_AVAILABLE) {
            LIB.value.SCardListReaders(handle, mszGroups, null, pcchReaders)
        }

        val neededLength = pcchReaders.value.toInt()

        // At least 3 bytes are needed to carry a reader name (1 byte) and 2 null terminators
        return if (hasReaders && neededLength >= 3) {
            val readers = ByteBuffer.allocateDirect(neededLength)

            wrapPCSCErrors {
                LIB.value.SCardListReaders(
                    handle,
                    mszGroups,
                    readers,
                    pcchReaders
                )
            }

            // TODO: https://youtrack.jetbrains.com/issue/KT-29974
            (readers as java.nio.Buffer).position(0)
            readers.getMultiString(neededLength).toList()
        } else {
            emptyList()
        }
    }

    // SCardConnect
    actual fun connect(reader: String, shareMode: ShareMode, preferredProtcols: Set<Protocol>?) : Card {
        val protocolMask = Dword(preferredProtcols?.toLong() ?: 0)
        val handle = nonNullHandle()

        val phCard = SCardHandleByReference()
        val pdwActiveProtocol = DwordByReference()

        wrapPCSCErrors {
            LIB.value.SCardConnect(handle, reader, shareMode.v, protocolMask, phCard, pdwActiveProtocol)
        }

        return Card(phCard.handle, pdwActiveProtocol.value.toLong().toProtocol())
    }

    // SCardGetStatusChange
    actual suspend fun getStatusChange(timeout: Int, readers: List<ReaderState>): List<ReaderState> {
        val handle = nonNullHandle()
        val readerCount = Dword(readers.size.toLong())

        // Copy the List<ReaderState> into an array with native types.
        // This will be passed by reference, and mutated...
        val jnaReaderStates = SCardReaderState.makeArray(readers.size)
        jnaReaderStates.forEachIndexed{ i, j -> j.copyFrom(readers[i]) }

        wrapPCSCErrors {
            LIB.value.SCardGetStatusChange(
                handle, Dword(timeout.toLong()), jnaReaderStates, readerCount)
        }

        return jnaReaderStates.toList().map { it.unwrap() }
    }

    /** Returns the handle associated with this context, or raises an exception if invalid. */
    private fun nonNullHandle(): SCardContext {
        val handle = handle
        require(handle != null) { "Context is invalid" }
        return handle
    }

    actual companion object {
        // SCardEstablishContext
        actual fun establish(scope: Scope) : Context {
            val phContext = SCardContextByReference()
            wrapPCSCErrors {
                LIB.value.SCardEstablishContext(
                    scope.v, null, null, phContext)
            }

            return Context(phContext.handle)
        }
    }
}
