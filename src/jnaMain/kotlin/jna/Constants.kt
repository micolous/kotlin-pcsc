/*
 * Constants.kt
 * Constants for JNA interface to Winscard API.
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
package au.id.micolous.kotlin.pcsc.jna

import com.sun.jna.Platform

private const val LIB_WIN32 = "WinSCard.dll"
private const val LIB_MACOS = "/System/Library/Frameworks/PCSC.framework/PCSC"
private const val LIB_NIX = "libpcsclite.so.1"

internal val LIB_NAME = when {
    Platform.isWindows() -> LIB_WIN32
    Platform.isMac() -> LIB_MACOS
    else -> LIB_NIX
}

internal const val MAX_BUFFER_SIZE = 264

// In SCARD_READERSTATE, Windows has 36 bytes for alignment; others have 33 bytes
internal const val MAX_ATR_SIZE = 33
