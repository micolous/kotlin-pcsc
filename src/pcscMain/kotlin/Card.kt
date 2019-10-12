package au.id.micolous.kotlin.pcsc

import au.id.micolous.kotlin.pcsc.internal.*
import kotlinx.cinterop.*
import platform.posix.*

class Card(private var handle: SCARDHANDLE) {
    // SCardDisconnect
    fun disconnect(disposition: DisconnectDisposition = DisconnectDisposition.Leave) {
        wrapPCSCErrors {
            SCardDisconnect(handle, disposition.v)
        }
    }

    // SCardReconnect
    fun reconnect(shareMode: ShareMode, preferredProtcols: Set<Protocol>?, initialization: Initialization) : Protocol? {
        val protocolMask = preferredProtcols?.toUInt() ?: 0u

        return memScoped {
            val dwActiveProtocol = alloc<uint32_tVar>()

            wrapPCSCErrors {
                SCardReconnect(handle, shareMode.v, protocolMask, initialization.v, dwActiveProtocol.ptr)
            }

            dwActiveProtocol.value.toProtocol()
        }
    }

    fun reconnect(shareMode: ShareMode, preferredProtocol: Protocol = Protocol.Any, initialization: Initialization) : Protocol?
            = reconnect(shareMode, setOf(preferredProtocol), initialization)
}
