package au.id.micolous.kotlin.pcsc

import au.id.micolous.kotlin.pcsc.internal.*
import kotlin.collections.MutableList
import kotlinx.cinterop.*
import platform.posix.*

class Context private constructor(private var handle: SCARDCONTEXT?) {
    enum class Scope(internal val dwScope: uint32_t) {
        User(SCARD_SCOPE_USER.toUInt()),
        Terminal(SCARD_SCOPE_TERMINAL.toUInt()),
        System(SCARD_SCOPE_SYSTEM.toUInt())
    }

    enum class ShareMode(internal val dwShareMode: uint32_t) {
        Shared(SCARD_SHARE_SHARED.toUInt()),
        Exclusive(SCARD_SHARE_EXCLUSIVE.toUInt()),
        Direct(SCARD_SHARE_DIRECT.toUInt())
    }

    // SCardReleaseContext
    fun release() {
        wrapPCSCErrors {
            SCardReleaseContext(handle!!)
        }
        handle = null
    }

    // SCardIsValidContext
    fun isValid() : Boolean {
        return wrapPCSCErrors(falseValue = SCARD_E_INVALID_HANDLE) {
            SCardIsValidContext(handle!!)
        }
    }

    // SCardListReaders
    fun listReaders(groups: List<String>? = null) : List<String> {
        return groups?.asMultiString().useNullablePinned { mszGroups -> memScoped {
            val pcchReaders = alloc<uint32_tVar>()

            // Figure out how much space we need for the buffer
            val hasReaders = wrapPCSCErrors(falseValue = SCARD_E_NO_READERS_AVAILABLE) {
                SCardListReaders(handle!!, mszGroups?.addressOf(0), null, pcchReaders.ptr)
            }

            if (hasReaders) {
                val readers = ByteArray(pcchReaders.value.toInt())
                readers.usePinned { mszReaders ->
                    wrapPCSCErrors {
                        SCardListReaders(
                            handle!!,
                            mszGroups?.addressOf(0),
                            mszReaders.addressOf(0),
                            pcchReaders.ptr
                        )
                    }
                }

                readers.toMultiString().toList()
            } else {
                listOf("foo")
            }
        }}
    }

    // SCardConnect
    fun connect(reader: String, shareMode: ShareMode, preferredProtcols: uint32_t) : Any? {

        return null
    }

    companion object {

        // SCardEstablishContext
        fun establish(scope: Scope) : Context {
            return Context(memScoped {
                val phContext = alloc<SCARDCONTEXTVar>()
                wrapPCSCErrors {
                    SCardEstablishContext(scope.dwScope, null, null, phContext.ptr)
                }
                phContext.value
            })
        }
    }

}
