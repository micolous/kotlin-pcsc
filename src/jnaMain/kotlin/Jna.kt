package au.id.micolous.kotlin.pcsc

import com.sun.jna.*
import com.sun.jna.ptr.ByReference
import java.lang.reflect.Method
import java.nio.ByteBuffer

private const val LIB_WIN32 = "WinSCard.dll"
private const val LIB_MACOS = "/System/Library/Frameworks/PCSC.framework/PCSC"
private const val LIB_NIX = "libpcsclite.so.1"

private val LIB_NAME = when {
    Platform.isWindows() -> LIB_WIN32
    Platform.isMac() -> LIB_MACOS
    else -> LIB_NIX
}

val DWORD_SIZE = if (Platform.isWindows() || Platform.isMac()) { 4 } else { NativeLong.SIZE }
val HANDLE_SIZE = if (Platform.isWindows()) { Pointer.SIZE } else { DWORD_SIZE }

internal open class KIntegerType(size: Int, value: Long = 0, unsigned: Boolean = true) : IntegerType(size, value, unsigned) {
    // This is needed to fill in the gaps of IntegerType's implementation of Number for Kotlin.
    override fun toByte() = toInt().toByte()
    override fun toChar() = toInt().toChar()
    override fun toShort() = toInt().toShort()
}

internal class Dword(value: Long = 0) : KIntegerType(DWORD_SIZE, value) {
    constructor() : this(0)
}

internal class DwordByReference() : ByReference(DWORD_SIZE) {
    constructor(value: Dword) : this() {
        this.value = value
    }

    init {
        internalSetValue()
    }

    var value: Dword
        get() = Dword(when (DWORD_SIZE) {
                4 -> pointer.getInt(0).toLong() and 0xffffffffL
                8 -> pointer.getLong(0)
                else -> throw NotImplementedError("DWORD_SIZE = $DWORD_SIZE")
            })

        set(value) = internalSetValue(value)

    private fun internalSetValue(value: Dword? = null) {
        when (DWORD_SIZE) {
            4 -> pointer.setInt(0, value?.toInt() ?: 0)
            8 -> pointer.setLong(0, value?.toLong() ?: 0)
            else -> throw NotImplementedError("DWORD_SIZE = $DWORD_SIZE")
        }
    }
}

internal class Handle(value: Long = 0) : KIntegerType(HANDLE_SIZE, value) {
}

internal class HandleByReference : ByReference(HANDLE_SIZE) {
    var value: Long
        get() = when (DWORD_SIZE) {
            4 -> pointer.getInt(0).toLong()
            8 -> pointer.getLong(0)
            else -> throw NotImplementedError("HANDLE_SIZE = $HANDLE_SIZE")
        }

        set(value) = when (DWORD_SIZE) {
            4 -> pointer.setInt(0, value.toInt())
            8 -> pointer.setLong(0, value)
            else -> throw NotImplementedError("HANDLE_SIZE = $HANDLE_SIZE")
        }

    val handle: Handle get() { return Handle(value) }
}

internal typealias SCardResult = Dword
internal typealias SCardContext = Handle
internal typealias SCardContextByReference = HandleByReference
internal typealias SCardHandle = Handle
internal typealias SCardHandleByReference = HandleByReference

// SCARD_IO_REQUEST
internal class SCardIoRequest(p: Pointer? = null) : Structure(p) {
    @JvmField var dwProtocol = Dword()
    @JvmField var cbPciLength = Dword()

    override fun getFieldOrder(): MutableList<Any?> {
        return mutableListOf("dwProtocol", "cbPciLength")
    }

    override fun toString(): String {
        return "${javaClass.name}{dwProtocol: $dwProtocol, cbPciLength: $cbPciLength}"
    }

    companion object {
        private fun build(dwProtocol: Dword): SCardIoRequest {
            return SCardIoRequest().apply {
                this.dwProtocol = dwProtocol
                this.cbPciLength = Dword(this.size().toLong())
            }
        }

        fun getForProtocol(protocol: Protocol?): SCardIoRequest =
            when (val p = protocol ?: Protocol.Undefined) {
                Protocol.T0 -> SCARD_PCI_T0.value
                Protocol.T1 -> SCARD_PCI_T1.value
                Protocol.Raw -> SCARD_PCI_RAW.value

                else -> build(Dword(p.v))
            }
    }
}

private fun getSCardIoRequest(v: String) = lazy {
    SCardIoRequest(NATIVE_LIB.value.getGlobalVariableAddress(v)).apply {
        read()
        setAutoSynch(false)
    }
}

private object MacOSFunctionMap: FunctionMapper {
    override fun getFunctionName(library: NativeLibrary, method: Method): String {
        return when (val name = method.name!!) {
            // SCardControl132 implements "standard" SCardControl signature in pcsc-lite 1.3.2 and
            // later. Older versions had a different signature.
            //
            // winscard.h in PCSC.framework has a #define for this.
            "SCardControl" -> "SCardControl132"
            else -> name
        }
    }
}

private object WindowsFunctionMap: FunctionMapper {
    override fun getFunctionName(library: NativeLibrary, method: Method): String {
        return when (val name = method.name!!) {
            // Remap all method calls to the ASCII versions.
            "SCardListReaderGroups",
            "SCardListReaders",
            "SCardGetStatusChange",
            "SCardConnect",
            "SCardStatus" -> name + "A"

            else -> name
        }
    }
}

@Suppress("FunctionName")
internal interface WinscardLibrary: Library {
    fun SCardEstablishContext(dwScope: Dword, pvReserved1: Pointer?, pvReserved2: Pointer?,
                              phContext: SCardContextByReference): SCardResult
    fun SCardReleaseContext(hContext: SCardContext): SCardResult
    fun SCardIsValidContext(hContext: SCardContext): SCardResult
    fun SCardListReaders(hContext: SCardContext, mszGroups: ByteArray?, mszReaders: ByteBuffer?,
                         pcchReaders: DwordByReference): SCardResult
    fun SCardConnect(hContext: SCardContext, szReader: String, dwShareMode: Dword,
                     dwPreferredProtocols: Dword, phCard: SCardHandleByReference,
                     pdwActiveProtocol: DwordByReference): SCardResult
    fun SCardReconnect(hCard: SCardHandle, dwShareMode: Dword, dwPreferredProtocols: Dword,
                       dwInitialization: Dword, pdwActiveProtocol: DwordByReference): SCardResult
    fun SCardDisconnect(hCard: SCardHandle, dwDisposition: Dword): SCardResult
    fun SCardTransmit(hCard: SCardHandle, pioSendPci: SCardIoRequest, pbSendBuffer: ByteArray,
                      cbSendLength: Dword, pioRecvPci: SCardIoRequest?, pbRecvBuffer: ByteBuffer,
                      pcbRecvLength: DwordByReference): SCardResult


}

internal val LIB = lazy {
    val options = mutableMapOf<String, Any>()
    when {
        Platform.isMac() -> options[Library.OPTION_FUNCTION_MAPPER] = MacOSFunctionMap
        Platform.isWindows() -> options[Library.OPTION_FUNCTION_MAPPER] = WindowsFunctionMap
    }

    Native.loadLibrary(LIB_NAME, WinscardLibrary::class.java, options) as WinscardLibrary
}

private val NATIVE_LIB = lazy { NativeLibrary.getInstance(LIB_NAME)!! }
internal val SCARD_PCI_T0 = getSCardIoRequest("g_rgSCardT0Pci")
internal val SCARD_PCI_T1 = getSCardIoRequest("g_rgSCardT1Pci")
internal val SCARD_PCI_RAW = getSCardIoRequest("g_rgSCardRawPci")
internal const val MAX_BUFFER_SIZE = 264
