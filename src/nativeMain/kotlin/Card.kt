package au.id.micolous.kotlin.pcsc

import au.id.micolous.kotlin.pcsc.internal.*
import kotlinx.cinterop.*
import platform.posix.*

actual class Card(
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
        val protocolMask = preferredProtcols?.toUInt() ?: 0u

        protocol = memScoped {
            val dwActiveProtocol = alloc<uint32_tVar>()

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
        val cbSendLength = mySendBuffer.size.toUInt()

        val pioSendPci = when (protocol) {
            Protocol.T0 -> SCARD_PCI_T0
            Protocol.T1 -> SCARD_PCI_T1
            Protocol.Raw -> SCARD_PCI_RAW
            // TODO: Implement other protocols
            else -> throw NotImplementedError("Protocol $protocol")
        }

        return memScoped { mySendBuffer.usePinned { pbSendBuffer ->
            val bRecvBuffer = UByteArray(MAX_BUFFER_SIZE)
            val pcbRecvLength = alloc<uint32_tVar>()
            pcbRecvLength.value = bRecvBuffer.size.toUInt()

            bRecvBuffer.usePinned { pbRecvBuffer -> wrapPCSCErrors {
                SCardTransmit(handle, pioSendPci, pbSendBuffer.addressOf(0), cbSendLength, null, pbRecvBuffer.addressOf(0), pcbRecvLength.ptr)
            }}

            bRecvBuffer.sliceArray(0..pcbRecvLength.value.toInt()).toByteArray()
        }}
    }
}
