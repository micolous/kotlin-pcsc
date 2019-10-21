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
    actual fun reconnect(shareMode: ShareMode, preferredProtcols: Set<Protocol>?, initialization: Initialization) {
        val protocolMask = Dword(preferredProtcols?.toLong() ?: 0)

        val pdwActiveProtocol = DwordByReference()
        wrapPCSCErrors {
            LIB.value.SCardReconnect(handle, shareMode.v, protocolMask, initialization.v, pdwActiveProtocol)
        }

        protocol = pdwActiveProtocol.value.toLong().toProtocol()

    }

    // SCardTransmit
    actual fun transmit(buffer: ByteArray) : ByteArray {
        // Copy the send buffer to insulate it from the library.
        val mySendBuffer = buffer.copyOf()
        val cbSendLength = Dword(mySendBuffer.size.toLong())

        val pioSendPci = SCardIoRequest.getForProtocol(protocol)

        val pbRecvBuffer = ByteBuffer.allocateDirect(MAX_BUFFER_SIZE)
        val pcbRecvLength = DwordByReference(Dword(pbRecvBuffer.limit().toLong()))

        wrapPCSCErrors {
            LIB.value.SCardTransmit(handle, pioSendPci, mySendBuffer, cbSendLength, null, pbRecvBuffer, pcbRecvLength)
        }

        pbRecvBuffer.position(0)
        return pbRecvBuffer.getByteArray(pcbRecvLength.value.toInt())
    }
}
