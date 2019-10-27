/*
 * SCardIoRequest.kt
 * SCARD_IO_REQUEST
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

import au.id.micolous.kotlin.pcsc.Protocol
import com.sun.jna.NativeLibrary
import com.sun.jna.Pointer
import com.sun.jna.Structure

private val NATIVE_LIB = lazy { NativeLibrary.getInstance(LIB_NAME)!! }
internal val SCARD_PCI_T0 = getSCardIoRequest("g_rgSCardT0Pci")
internal val SCARD_PCI_T1 = getSCardIoRequest("g_rgSCardT1Pci")
internal val SCARD_PCI_RAW = getSCardIoRequest("g_rgSCardRawPci")

// SCARD_IO_REQUEST
internal class SCardIoRequest(p: Pointer? = null) : Structure(p) {
    @JvmField var dwProtocol = Dword()
    @JvmField var cbPciLength = Dword()

    override fun getFieldOrder(): MutableList<Any?> {
        return mutableListOf("dwProtocol", "cbPciLength")
    }

    override fun toString(): String {
        return "${javaClass.name}{dwProtocol: $dwProtocol, cbPciLength: $cbPciLength}"
    }

    companion object {
        private fun build(dwProtocol: Dword): SCardIoRequest {
            return SCardIoRequest().apply {
                this.dwProtocol = dwProtocol
                this.cbPciLength = Dword(this.size().toLong())
            }
        }

        fun getForProtocol(protocol: Protocol?): SCardIoRequest =
            when (val p = protocol ?: Protocol.Undefined) {
                Protocol.T0 -> SCARD_PCI_T0.value
                Protocol.T1 -> SCARD_PCI_T1.value
                Protocol.Raw -> SCARD_PCI_RAW.value

                else -> build(Dword(p.v))
            }
    }
}

private fun getSCardIoRequest(v: String) = lazy {
    SCardIoRequest(NATIVE_LIB.value.getGlobalVariableAddress(v)).apply {
        read()
        setAutoSynch(false)
    }
}

