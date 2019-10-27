/*
 * PlatformTypes.kt
 * Type aliases for macOS on Kotlin/Native.
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

import kotlinx.cinterop.*
import platform.posix.*

/*
 * These typealiases are required because using Kotlin Native ObjC/Interop doesn't expose #defines
 * or typedefs declared in winscard.def, and PCSC.framework/Modules/module.modulemap excludes types
 * declared (eg: DWORD) that were declared in PCSC.framework/Headers/wintypes.h.
 */
typealias DWORD = uint32_t;
typealias DWORDVar = uint32_tVar;

// Return type for all SCard* functions.
typealias SCARDSTATUS = int32_t;
