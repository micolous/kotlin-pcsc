/*
 * Common.kt
 * Native implementation of common definitions
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
import kotlin.native.Platform
import kotlinx.cinterop.*

internal actual val isLittleEndian = Platform.isLittleEndian
internal val DWORD_ZERO = 0.convert<DWORD>()

@Suppress("NOTHING_TO_INLINE")
internal inline fun DWORD.hasBits(mask: DWORD) = (this and mask) == mask
