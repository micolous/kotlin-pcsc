/*
 * PCSCError.kt
 * Error handling helpers for native PC/SC API
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