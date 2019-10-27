/*
 * PCSCError.kt
 * Error class for PC/SC API
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

data class PCSCError private constructor(
    val code: Long,
    /** Canonical enumeration for this error condition */
    val error: PCSCErrorCode?) : Throwable() {
    internal constructor(error: PCSCErrorCode) : this(error.code, error)

    override val message: String
        get() = error?.message ?: "UNKNOWN (0x${code.toString(16)})"

    override fun toString() = "PCSCError: $message"

    companion object {
        /** Converts a numeric error code to a [PCSCError] */
        internal fun fromCode(errorCode: Long) = PCSCErrorCode.values().find {
            it.code == errorCode
        }?.let { PCSCError(it) } ?: PCSCError(errorCode, null)

        internal fun fromCode(errorCode: Int) =
            fromCode(errorCode.toUInt().toLong())

        internal fun fromCode(errorCode: UInt) =
            fromCode(errorCode.toLong())
    }
}
