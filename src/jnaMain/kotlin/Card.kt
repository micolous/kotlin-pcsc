/*
 * Card.kt
 * JNA implementation of SCARDHANDLE PC/SC operations
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

actual class Card internal constructor(
    private val handle: SCardHandle,
    actual var protocol: Protocol?) {

    // SCardDisconnect
    actual fun disconnect(disposition: DisconnectDisposition) {
        wrapPCSCErrors {
            LIB.value.SCardDisconnect(handle, disposition.v)
        }
        protocol = null
    }

    // SCardReconnect
    actual fun reconnect(
        shareMode: ShareMode,
        preferredProtcols: Set<Protocol>?,
        initialization: Initialization
    ) {
        val protocolMask = Dword(preferredProtcols?.toLong() ?: 0)

        val pdwActiveProtocol = DwordByReference()
        wrapPCSCErrors {
            LIB.value.SCardReconnect(
                handle,
                shareMode.v,
                protocolMask,
                initialization.v,
                pdwActiveProtocol
            )
        }

        protocol = pdwActiveProtocol.value.toLong().toProtocol()

    }

    // SCardTransmit
    actual fun transmit(buffer: ByteArray): ByteArray {
        // Copy the send buffer to insulate it from the library.
        val mySendBuffer = buffer.copyOf()
        val cbSendLength = Dword(mySendBuffer.size.toLong())

        val pioSendPci = SCardIoRequest.getForProtocol(protocol)

        val pbRecvBuffer = ByteBuffer.allocateDirect(MAX_BUFFER_SIZE)
        val pcbRecvLength = DwordByReference(Dword(pbRecvBuffer.limit().toLong()))

        wrapPCSCErrors {
            LIB.value.SCardTransmit(
                handle,
                pioSendPci,
                mySendBuffer,
                cbSendLength,
                null,
                pbRecvBuffer,
                pcbRecvLength
            )
        }

        pbRecvBuffer.position(0)
        return pbRecvBuffer.getByteArray(pcbRecvLength.value.toInt())
    }

    // SCardBeginTransaction
    actual fun beginTransaction() {
        wrapPCSCErrors {
            LIB.value.SCardBeginTransaction(handle)
        }
    }

    // SCardEndTransaction
    actual fun endTransaction(disposition: DisconnectDisposition) {
        wrapPCSCErrors {
            LIB.value.SCardEndTransaction(handle, disposition.v)
        }
    }

    // SCardStatus
    actual fun status(): CardStatus {
        val pcchReaderLen = DwordByReference()
        val pcbAtrLen = DwordByReference()

        // Figure out how much space we need for the buffer
        wrapPCSCErrors(falseValue = PCSCErrorCode.E_INSUFFICIENT_BUFFER) {
            LIB.value.SCardStatus(handle, null, pcchReaderLen, null, null, null, pcbAtrLen)
        }

        val readerNames = ByteBuffer.allocateDirect(pcchReaderLen.value.toInt())
        val atr = ByteBuffer.allocateDirect(pcbAtrLen.value.toInt())
        val pdwState = DwordByReference()
        val pdwProtocol = DwordByReference()

        wrapPCSCErrors {
            LIB.value.SCardStatus(
                handle,
                readerNames,
                pcchReaderLen,
                pdwState,
                pdwProtocol,
                atr,
                pcbAtrLen
            )
        }

        readerNames.position(0)
        atr.position(0)

        val state = pdwState.value.toLong()
        return CardStatus(
            readerNames = readerNames.getMultiString(pcchReaderLen.value.toInt()).toList(),
            // SCARD_UNKNOWN == 0 on Windows
            unknown = ((SCARD_UNKNOWN == 0L && state == SCARD_UNKNOWN) ||
                    (SCARD_UNKNOWN != 0L && (state and SCARD_UNKNOWN) == SCARD_UNKNOWN)),
            // These are all written as bitmasks to be compatible with pcsclite and Windows
            absent = (state and SCARD_ABSENT) == SCARD_ABSENT,
            present = (state and SCARD_PRESENT) == SCARD_PRESENT,
            swallowed = (state and SCARD_SWALLOWED) == SCARD_SWALLOWED,
            powered = (state and SCARD_POWERED) == SCARD_POWERED,
            negotiable = (state and SCARD_NEGOTIABLE) == SCARD_NEGOTIABLE,
            specific = (state and SCARD_SPECIFIC) == SCARD_SPECIFIC,
            protocol = pdwProtocol.value.toLong().toProtocol(),
            atr = atr.getByteArray(pcbAtrLen.value.toInt())
        )
    }

    // SCardControl
    actual fun control(controlCode: Long, sendBuffer: ByteArray?, recvBufferSize: Int): ByteArray? {
        require(recvBufferSize >= 0) { "recvBufferSize must be >= 0" }
        val bSendBuffer = sendBuffer?.copyOf()
        val cbSendLength = Dword(bSendBuffer?.size?.toLong() ?: 0)
        val bRecvBuffer =
            if (recvBufferSize == 0) null else ByteBuffer.allocateDirect(recvBufferSize)
        val cbRecvLength = Dword(recvBufferSize.toLong())
        val lpBytesReturned = DwordByReference()

        wrapPCSCErrors {
            LIB.value.SCardControl(
                handle,
                Dword(controlCode),
                bSendBuffer,
                cbSendLength,
                bRecvBuffer,
                cbRecvLength,
                lpBytesReturned
            )
        }

        return bRecvBuffer?.run {
            position(0)
            getByteArray(lpBytesReturned.value.toInt())
        }
    }

    // SCardGetAttrib
    actual fun getAttrib(attribute: Long): ByteArray? {
        val dwAttrId: Dword = Dword(attribute)

        // Figure out how much space we need for the buffer, and if the attribute is supported
        val pcbAttrLen = DwordByReference()
        if (!wrapPCSCErrors(falseValue = PCSCErrorCode.E_UNEXPECTED) {
                LIB.value.SCardGetAttrib(handle, dwAttrId, null, pcbAttrLen)
            }) {
            // Unsupported function
            return null
        }

        val neededLength = pcbAttrLen.value.toInt()
        if (neededLength == 0) {
            // Don't need any buffer for this, return empty array now.
            return ByteArray(0)
        }

        val bAttr = ByteBuffer.allocateDirect(neededLength)!!

        wrapPCSCErrors {
            LIB.value.SCardGetAttrib(handle, dwAttrId, bAttr, pcbAttrLen)
        }

        return bAttr.run {
            position(0)
            getByteArray(pcbAttrLen.value.toInt())
        }
    }
}
