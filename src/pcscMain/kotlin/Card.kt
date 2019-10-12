package au.id.micolous.kotlin.pcsc

import au.id.micolous.kotlin.pcsc.internal.*
import kotlinx.cinterop.*
import platform.posix.*

actual class Card(private var handle: SCARDHANDLE) {
    // SCardDisconnect
    actual fun disconnect(disposition: DisconnectDisposition) {
        wrapPCSCErrors {
            SCardDisconnect(handle, disposition.v)
        }
    }

    // SCardReconnect
    actual fun reconnect(shareMode: ShareMode, preferredProtcols: Set<Protocol>?, initialization: Initialization) : Protocol? {
        val protocolMask = preferredProtcols?.toUInt() ?: 0u

        return memScoped {
            val dwActiveProtocol = alloc<uint32_tVar>()

            wrapPCSCErrors {
                SCardReconnect(handle, shareMode.v, protocolMask, initialization.v, dwActiveProtocol.ptr)
            }

            dwActiveProtocol.value.toProtocol()
        }
    }
}
