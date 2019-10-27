/*
 * WinscardLibrary.kt
 * JNA Library interface for Winscard API.
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

import com.sun.jna.Library
import com.sun.jna.Pointer
import java.nio.ByteBuffer


@Suppress("FunctionName")
internal interface WinscardLibrary: Library {
    fun SCardEstablishContext(dwScope: Dword, pvReserved1: Pointer?, pvReserved2: Pointer?,
                              phContext: SCardContextByReference): SCardResult
    fun SCardReleaseContext(hContext: SCardContext): SCardResult
    fun SCardIsValidContext(hContext: SCardContext): SCardResult
    fun SCardListReaders(hContext: SCardContext, mszGroups: ByteArray?, mszReaders: ByteBuffer?,
                         pcchReaders: DwordByReference): SCardResult
    fun SCardConnect(hContext: SCardContext, szReader: String, dwShareMode: Dword,
                     dwPreferredProtocols: Dword, phCard: SCardHandleByReference,
                     pdwActiveProtocol: DwordByReference): SCardResult
    fun SCardReconnect(hCard: SCardHandle, dwShareMode: Dword, dwPreferredProtocols: Dword,
                       dwInitialization: Dword, pdwActiveProtocol: DwordByReference): SCardResult
    fun SCardDisconnect(hCard: SCardHandle, dwDisposition: Dword): SCardResult
    fun SCardTransmit(hCard: SCardHandle, pioSendPci: SCardIoRequest, pbSendBuffer: ByteArray,
                      cbSendLength: Dword, pioRecvPci: SCardIoRequest?, pbRecvBuffer: ByteBuffer,
                      pcbRecvLength: DwordByReference): SCardResult
    fun SCardBeginTransaction(hCard: SCardHandle): SCardResult
    fun SCardEndTransaction(hCard: SCardHandle, dwDisposition: Dword): SCardResult
    fun SCardCancel(hContext: SCardContext): SCardResult
    fun SCardStatus(hCard: SCardHandle, mszReaderName: ByteBuffer?,
                    pcchReaderLen: DwordByReference, pdwState: DwordByReference?,
                    pdwProtocol: DwordByReference?, pbAtr: ByteBuffer?,
                    pcbAtrLen: DwordByReference) : SCardResult
    fun SCardControl(hCard: SCardHandle, dwControlCode: Dword, pbSendBuffer: ByteArray?,
                     cbSendLength: Dword, pbRecvBuffer: ByteBuffer?, cbRecvLength: Dword,
                     lpBytesReturned: DwordByReference) : SCardResult
    fun SCardGetAttrib(hCard: SCardHandle, dwAttrId: Dword, pbAttr: ByteBuffer?,
                       pcbAttrLen: DwordByReference) : SCardResult
}