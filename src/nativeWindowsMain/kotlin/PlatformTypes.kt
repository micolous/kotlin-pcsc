/*
 * PlatformTypes.kt
 * Type aliases for Windows on Kotlin/Native.
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
package au.id.micolous.kotlin.pcsc.internal

/*
 * This makes common Windows types (also used in pcsclite) look like what we get out of pcsclite.
 */
internal typealias DWORD = platform.windows.DWORD
internal typealias DWORDVar = platform.windows.DWORDVar
internal typealias SCARD_READERSTATE = platform.windows.SCARD_READERSTATE_A

/*
 * These are required because Kotlin/Native's windows.def massively overreaches across the Win32
 * API, by inclusion of windows.h without any headerFilter.
 */
internal typealias SCARDHANDLE = platform.windows.SCARDHANDLE
internal typealias SCARDHANDLEVar = platform.windows.SCARDHANDLEVar
internal typealias SCARDCONTEXT = platform.windows.SCARDCONTEXT
internal typealias SCARDCONTEXTVar = platform.windows.SCARDCONTEXTVar

internal const val SCARD_PROTOCOL_T0 = platform.windows.SCARD_PROTOCOL_T0
internal const val SCARD_PROTOCOL_T1 = platform.windows.SCARD_PROTOCOL_T1
internal const val SCARD_PROTOCOL_RAW = platform.windows.SCARD_PROTOCOL_RAW
internal const val SCARD_PROTOCOL_UNDEFINED = platform.windows.SCARD_PROTOCOL_UNDEFINED

internal const val SCARD_SCOPE_USER = platform.windows.SCARD_SCOPE_USER
internal const val SCARD_SCOPE_TERMINAL = platform.windows.SCARD_SCOPE_TERMINAL
internal const val SCARD_SCOPE_SYSTEM = platform.windows.SCARD_SCOPE_SYSTEM
internal const val SCARD_SHARE_SHARED = platform.windows.SCARD_SHARE_SHARED
internal const val SCARD_SHARE_EXCLUSIVE = platform.windows.SCARD_SHARE_EXCLUSIVE
internal const val SCARD_SHARE_DIRECT = platform.windows.SCARD_SHARE_DIRECT

internal const val SCARD_LEAVE_CARD = platform.windows.SCARD_LEAVE_CARD
internal const val SCARD_RESET_CARD = platform.windows.SCARD_RESET_CARD
internal const val SCARD_UNPOWER_CARD = platform.windows.SCARD_UNPOWER_CARD
internal const val SCARD_EJECT_CARD = platform.windows.SCARD_EJECT_CARD

internal const val SCARD_NEGOTIABLE = platform.windows.SCARD_NEGOTIABLE
internal const val SCARD_POWERED = platform.windows.SCARD_POWERED
internal const val SCARD_SWALLOWED = platform.windows.SCARD_SWALLOWED
internal const val SCARD_PRESENT = platform.windows.SCARD_PRESENT
internal const val SCARD_ABSENT = platform.windows.SCARD_ABSENT
internal const val SCARD_UNKNOWN = platform.windows.SCARD_UNKNOWN
internal const val SCARD_SPECIFIC = platform.windows.SCARD_SPECIFIC

/*
 * These are wrapped as properties.
 */
internal val SCARD_PCI_T0 = platform.windows.SCARD_PCI_T0!!
internal val SCARD_PCI_T1 = platform.windows.SCARD_PCI_T1!!
internal val SCARD_PCI_RAW = platform.windows.SCARD_PCI_RAW!!

/*
 * These are also properties that point to functions. SCardConnect isn't here, because it doesn't
 * work right unless it's an actual function (due to string conversion).
 */
internal val SCardDisconnect = WCardDisconnect!!
internal val SCardGetAttrib = WCardGetAttrib!!
internal val SCardReconnect = WCardReconnect!!
internal val SCardTransmit = WCardTransmit!!
internal val SCardBeginTransaction = WCardBeginTransaction!!
internal val SCardEndTransaction = WCardEndTransaction!!
internal val SCardStatus = WCardStatus!!
internal val SCardReleaseContext = WCardReleaseContext!!
internal val SCardIsValidContext = WCardIsValidContext!!
internal val SCardCancel = WCardCancel!!
internal val SCardListReaders = WCardListReaders!!
internal val SCardEstablishContext = WCardEstablishContext!!
internal val SCardGetStatusChange = WCardGetStatusChange!!
