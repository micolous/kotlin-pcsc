/*
 * Jna.kt
 * JNA wrapper interface for Winscard API.
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

import com.sun.jna.*
import java.lang.reflect.Method

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

internal val LIB = lazy {
    val options = mutableMapOf<String, Any>()
    when {
        Platform.isMac() -> options[Library.OPTION_FUNCTION_MAPPER] = MacOSFunctionMap
        Platform.isWindows() -> options[Library.OPTION_FUNCTION_MAPPER] = WindowsFunctionMap
    }

    Native.loadLibrary(LIB_NAME, WinscardLibrary::class.java, options) as WinscardLibrary
}
