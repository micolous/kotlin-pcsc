/*
 * Context.kt
 * JNA implementation of PC/SC API enumerations
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

import au.id.micolous.kotlin.pcsc.jna.Dword
import com.sun.jna.Platform

actual enum class Scope(private val l: Long) {
    User(0),
    Terminal(1),
    System(2);

    internal val v = Dword(l)
}

actual enum class ShareMode(private val l: Long) {
    Shared(2),
    Exclusive(1),
    Direct(3);

    internal val v = Dword(l)
}

actual enum class Protocol(internal val v: Long) {
    Unset(0),
    T0(1),
    T1(2),
    Any(T0.v or T1.v),
    T15(0x8), // not supported on Windows
    Raw(if (Platform.isWindows()) { 0x10000 } else { 0x4 }),
    Undefined(0);
}

internal fun Long.toProtocol(): Protocol? {
    return Protocol.values().find { it.v == this }
}

internal fun Set<Protocol>.toLong(): Long {
    return map { it.v }.reduce { acc, it -> acc or it }
}

actual enum class DisconnectDisposition(private val l: Long) {
    Leave(0),
    Reset(1),
    Unpower(2),
    Eject(3);

    internal val v = Dword(l)
}

actual enum class Initialization(private val l: Long) {
    Leave(0),
    Reset(1),
    Unpower(2);

    internal val v = Dword(l)
}
