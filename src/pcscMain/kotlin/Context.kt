package au.id.micolous.kotlin.pcsc

import au.id.micolous.kotlin.pcsc.internal.*
import kotlinx.cinterop.*
import platform.posix.uint32_t

class PCSCContext private constructor(private var handle: SCARDCONTEXT?) {
    enum class Scope(internal val dwScope: uint32_t) {
        User(SCARD_SCOPE_USER.toUInt()),
        Terminal(SCARD_SCOPE_TERMINAL.toUInt()),
        System(SCARD_SCOPE_SYSTEM.toUInt())
    }

    fun release() {
        wrapPCSCErrors {
            SCardReleaseContext(handle!!)
        }
        handle = null
    }

    fun isValid() : Boolean {
        return wrapPCSCErrors(falseValue = SCARD_E_INVALID_HANDLE) {
            SCardIsValidContext(handle!!)
        }
    }

    companion object {
        fun establish(scope: Scope) : PCSCContext {
            return PCSCContext(memScoped {
                val phContext = alloc<SCARDCONTEXTVar>()
                wrapPCSCErrors {
                    SCardEstablishContext(scope.dwScope, null, null, phContext.ptr)
                }
                phContext.value
            })
        }
    }

}
