package au.id.micolous.kotlin.pcsc

import au.id.micolous.kotlin.pcsc.internal.*
import platform.posix.int32_t

internal fun wrapPCSCErrors(
    trueValue: Int = SCARD_S_SUCCESS,
    falseValue: Int? = null,
    f: () -> int32_t): Boolean {
    return when (val errorCode = f()) {
        trueValue -> true
        falseValue -> false
        else -> throw PCSCError(errorCode.toLong())
    }
}

internal fun wrapPCSCErrors(
    trueValue: Int = SCARD_S_SUCCESS,
    falseValue: UInt,
    f: () -> int32_t) : Boolean {
    return wrapPCSCErrors(trueValue, falseValue.toInt(), f)
}