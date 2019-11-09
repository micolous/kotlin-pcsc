/*
 * Card.kt
 * Native implementation of SCARDHANDLE PC/SC operations
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
import au.id.micolous.kotlin.pcsc.native.*
import kotlinx.cinterop.*
import platform.posix.*

actual class Card internal constructor(
    private val handle: SCARDHANDLE,
    actual var protocol: Protocol?) {

    // SCardDisconnect
    actual fun disconnect(disposition: DisconnectDisposition) {
        wrapPCSCErrors {
            SCardDisconnect(handle, disposition.v)
        }
        protocol = null
    }

    // SCardReconnect
    actual fun reconnect(shareMode: ShareMode, preferredProtcols: Set<Protocol>?, initialization: Initialization) {
        val protocolMask: DWORD = preferredProtcols?.toDWord() ?: 0u

        protocol = memScoped {
            val dwActiveProtocol = alloc<DWORDVar>()

            wrapPCSCErrors {
                SCardReconnect(handle, shareMode.v, protocolMask, initialization.v, dwActiveProtocol.ptr)
            }

            dwActiveProtocol.value.toProtocol()
        }
    }

    // SCardTransmit
    actual fun transmit(buffer: ByteArray) : ByteArray {
        // Copy the send buffer to insulate it from the library.
        val mySendBuffer = buffer.toUByteArray()
        val cbSendLength: DWORD = mySendBuffer.size.convert()

        val pioSendPci = when (protocol) {
            Protocol.T0 -> SCARD_PCI_T0
            Protocol.T1 -> SCARD_PCI_T1
            Protocol.Raw -> SCARD_PCI_RAW
            // TODO: Implement other protocols
            else -> throw NotImplementedError("Protocol $protocol")
        }

        return memScoped { mySendBuffer.usePinned { pbSendBuffer ->
            val bRecvBuffer = UByteArray(MAX_BUFFER_SIZE)
            val pcbRecvLength = alloc<DWORDVar>()
            pcbRecvLength.value = bRecvBuffer.size.convert()

            bRecvBuffer.usePinned { pbRecvBuffer -> wrapPCSCErrors {
                SCardTransmit(handle, pioSendPci, pbSendBuffer.addressOf(0), cbSendLength, null, pbRecvBuffer.addressOf(0), pcbRecvLength.ptr)
            }}

            bRecvBuffer.sliceArray(0..pcbRecvLength.value.toInt()).toByteArray()
        }}
    }

    // SCardBeginTransaction
    actual fun beginTransaction() {
        wrapPCSCErrors {
            SCardBeginTransaction(handle)
        }
    }

    // SCardEndTransaction
    actual fun endTransaction(disposition: DisconnectDisposition) {
        wrapPCSCErrors {
            SCardEndTransaction(handle, disposition.v)
        }
    }

    // SCardStatus
    actual fun status(): CardStatus {
        return memScoped {
            val pcchReaderLen = alloc<DWORDVar>()
            val pcbAtrLen = alloc<DWORDVar>()

            // Figure out how much space we need for the buffer
            wrapPCSCErrors(falseValue = SCARD_E_INSUFFICIENT_BUFFER) {
                SCardStatus(handle, null, pcchReaderLen.ptr, null, null, null, pcbAtrLen.ptr)
            }

            // We now have correct buffer sizes
            val readerNames = maybeByteArray(pcchReaderLen)
            val atr = maybeUByteArray(pcbAtrLen)
            val pdwState = alloc<DWORDVar>()
            val pdwProtocol = alloc<DWORDVar>()

            readerNames.useNullablePinned { mszReaderNames -> atr.useNullablePinned { pbAtr ->
                wrapPCSCErrors {
                    SCardStatus(
                        handle,
                        mszReaderNames?.addressOf(0),
                        pcchReaderLen.ptr,
                        pdwState.ptr,
                        pdwProtocol.ptr,
                        pbAtr?.addressOf(0),
                        pcbAtrLen.ptr
                    )
                }
            }}

            val state = pdwState.value
            CardStatus(
                readerNames = readerNames?.toMultiString()?.toList() ?: emptyList(),
                // SCARD_UNKNOWN == 0 on Windows
                unknown = state == SCARD_UNKNOWN.convert<DWORD>(),
                // These are all written as bitmasks to be compatible with pcsclite and Windows
                absent = (state and SCARD_ABSENT.convert<DWORD>()) == SCARD_ABSENT.convert<DWORD>(),
                present = (state and SCARD_PRESENT.convert<DWORD>()) == SCARD_PRESENT.convert<DWORD>(),
                swallowed = (state and SCARD_SWALLOWED.convert<DWORD>()) == SCARD_SWALLOWED.convert<DWORD>(),
                powered = (state and SCARD_POWERED.convert<DWORD>()) == SCARD_POWERED.convert<DWORD>(),
                negotiable = (state and SCARD_NEGOTIABLE.convert<DWORD>()) == SCARD_NEGOTIABLE.convert<DWORD>(),
                specific = (state and SCARD_SPECIFIC.convert<DWORD>()) == SCARD_SPECIFIC.convert<DWORD>(),
                protocol = pdwProtocol.value.toProtocol(),
                atr = atr?.toByteArray() ?: ByteArray(0)
            )
        }
    }

    // SCardControl
    actual fun control(controlCode: Long, sendBuffer: ByteArray?, recvBufferSize: Int): ByteArray? {
        require(recvBufferSize >= 0) { "recvBufferSize must be >= 0" }
        val bSendBuffer = sendBuffer?.toUByteArray()
        val cbSendLength: DWORD = (bSendBuffer?.size ?: 0).convert()
        val bRecvBuffer = maybeUByteArray(recvBufferSize)
        var cbRecvLength: DWORD = (bSendBuffer?.size ?: 0).convert()

        return memScoped {
            val lpBytesReturned = alloc<DWORDVar>()

            sendBuffer.useNullablePinned { pbSendBuffer ->
                bRecvBuffer.useNullablePinned { pbRecvBuffer -> wrapPCSCErrors {
                    SCardControl132(
                        handle,
                        controlCode.convert<DWORD>(),
                        pbSendBuffer?.addressOf(0),
                        cbSendLength,
                        pbRecvBuffer?.addressOf(0),
                        cbRecvLength,
                        lpBytesReturned.ptr
                    )
                }}
            }

            bRecvBuffer?.sliceArray(0 until lpBytesReturned.value.toInt())?.toByteArray()
        }
    }

    // SCardGetAttrib
    actual fun getAttrib(attribute: Long) : ByteArray? {
        val dwAttrId: DWORD = attribute.convert()
        return memScoped {
            // Figure out how much space we need for the buffer, and if the attribute is supported
            val pcbAttrLen = alloc<DWORDVar>()
            if (!wrapPCSCErrors(falseValue = SCARD_E_UNEXPECTED) {
                SCardGetAttrib(handle, dwAttrId, null, pcbAttrLen.ptr)
            }) {
                // Unsupported function
                return null
            }

            val neededLength = pcbAttrLen.value.toInt()
            if (neededLength == 0) {
                // Don't need any buffer for this, return empty array now.
                return ByteArray(0)
            }

            val bAttr = UByteArray(neededLength)
            bAttr.usePinned { pbAttr -> wrapPCSCErrors {
                SCardGetAttrib(handle, dwAttrId, pbAttr.addressOf(0), pcbAttrLen.ptr)
            }}

            bAttr.sliceArray(0 until pcbAttrLen.value.toInt()).toByteArray()
        }
    }

}
