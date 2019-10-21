/*
 * Context.kt
 * Native implementation of SCARDCONTEXT PC/SC operations
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

import au.id.micolous.kotlin.pcsc.internal.*
import kotlinx.cinterop.*
import platform.posix.*

actual class Context private constructor(private var handle: SCARDCONTEXT?) {

    // SCardReleaseContext
    actual fun release() {
        val handle = handle ?: return
        wrapPCSCErrors {
            SCardReleaseContext(handle)
        }
        this.handle = null
    }

    // SCardIsValidContext
    actual fun isValid() : Boolean {
        return when (val handle = handle) {
            null -> false
            else -> wrapPCSCErrors(falseValue = SCARD_E_INVALID_HANDLE) {
                SCardIsValidContext(handle)
            }
        }
    }

    /** Returns the handle associated with this context, or raises an exception if invalid. */
    private fun nonNullHandle(): SCARDCONTEXT {
        val handle = handle
        require(handle != null) { "Context is invalid" }
        return handle
    }

    // SCardListReaders
    actual fun listReaders(groups: List<String>?) : List<String> {
        val handle = nonNullHandle()
        return groups?.asMultiString().useNullablePinned { mszGroups -> memScoped {
            val pcchReaders = alloc<DWORDVar>()

            // Figure out how much space we need for the buffer
            val hasReaders = wrapPCSCErrors(falseValue = SCARD_E_NO_READERS_AVAILABLE) {
                SCardListReaders(handle, mszGroups?.addressOf(0), null, pcchReaders.ptr)
            }

            val neededLength = pcchReaders.value.toInt()
            // At least 3 bytes are needed to carry a reader name (1 byte) and 2 null terminators
            if (hasReaders && neededLength >= 3) {
                val readers = ByteArray(neededLength)
                readers.usePinned { mszReaders ->
                    wrapPCSCErrors {
                        SCardListReaders(
                            handle,
                            mszGroups?.addressOf(0),
                            mszReaders.addressOf(0),
                            pcchReaders.ptr
                        )
                    }
                }

                readers.toMultiString().toList()
            } else {
                emptyList()
            }
        }}
    }

    // SCardConnect
    actual fun connect(reader: String, shareMode: ShareMode, preferredProtcols: Set<Protocol>?) : Card {
        val protocolMask: DWORD = preferredProtcols?.toDWord() ?: 0u
        val handle = nonNullHandle()

        return memScoped {
            val hCard = alloc<SCARDHANDLEVar>()
            val dwActiveProtocol = alloc<DWORDVar>()

            wrapPCSCErrors {
                SCardConnect(handle, reader, shareMode.v, protocolMask, hCard.ptr, dwActiveProtocol.ptr)
            }

            Card(hCard.value, dwActiveProtocol.value.toProtocol())
        }
    }

    actual companion object {
        // SCardEstablishContext
        actual fun establish(scope: Scope) : Context {
            return Context(memScoped {
                val phContext = alloc<SCARDCONTEXTVar>()
                wrapPCSCErrors {
                    SCardEstablishContext(scope.v, null, null, phContext.ptr)
                }
                phContext.value
            })
        }
    }
}
