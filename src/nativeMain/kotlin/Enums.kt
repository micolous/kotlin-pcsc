/*
 * Context.kt
 * Native implementation of PC/SC API enumerations
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
import platform.posix.*

actual enum class Scope(internal val v: uint32_t) {
    User(SCARD_SCOPE_USER.toUInt()),
    Terminal(SCARD_SCOPE_TERMINAL.toUInt()),
    System(SCARD_SCOPE_SYSTEM.toUInt())
}

actual enum class ShareMode(internal val v: uint32_t) {
    Shared(SCARD_SHARE_SHARED.toUInt()),
    Exclusive(SCARD_SHARE_EXCLUSIVE.toUInt()),
    Direct(SCARD_SHARE_DIRECT.toUInt())
}

actual enum class Protocol(internal val v: uint32_t) {
    Unset(SCARD_PROTOCOL_UNSET.toUInt()),
    Any(SCARD_PROTOCOL_ANY.toUInt()),
    T0(SCARD_PROTOCOL_T0.toUInt()),
    T1(SCARD_PROTOCOL_T1.toUInt()),
    T15(SCARD_PROTOCOL_T15.toUInt()),
    Raw(SCARD_PROTOCOL_RAW.toUInt()),
    Undefined(SCARD_PROTOCOL_UNDEFINED.toUInt());
}

internal fun uint32_t.toProtocol(): Protocol? {
    return Protocol.values().find { it.v == this }
}

internal fun Set<Protocol>.toUInt(): uint32_t {
    return map { it.v }.reduce { acc, it -> acc or it }
}

actual enum class DisconnectDisposition(internal val v: uint32_t) {
    Leave(SCARD_LEAVE_CARD.toUInt()),
    Reset(SCARD_RESET_CARD.toUInt()),
    Unpower(SCARD_UNPOWER_CARD.toUInt()),
    Eject(SCARD_EJECT_CARD.toUInt())
}

actual enum class Initialization(internal val v: uint32_t) {
    Leave(SCARD_LEAVE_CARD.toUInt()),
    Reset(SCARD_RESET_CARD.toUInt()),
    Unpower(SCARD_UNPOWER_CARD.toUInt())
}
