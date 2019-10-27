/*
 * Sample.kt
 * Implementation of PC/SC Sample Application using kotlin-pcsc
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
@file:JvmName("SampleKt")
import au.id.micolous.kotlin.pcsc.*
import kotlin.jvm.JvmName

/** Converts signed [Int] values into a [ByteArray] */
fun byteArrayOf(vararg i: Int) : ByteArray = i.map(Int::toByte).toByteArray()

/** Converts a [ByteArray] to a space-separated hex string for display */
fun ByteArray.toHex(): String = map {
    it.toUByte().toString(16).padStart(2, '0').padEnd(3, ' ')
}.fold(StringBuilder(), StringBuilder::append).dropLast(1).toString()

/** APDU: SELECT `A00000006203010C0601` */
val cmd1 = byteArrayOf(
    0x00, 0xA4, 0x04, 0x00, 0x0A, 0xA0, 0x00, 0x00, 0x00, 0x62, 0x03, 0x01,
    0x0C, 0x06, 0x01
)

/** APDU: CLA=00, INS=00, P1=00, P2=00 */
val cmd2 = byteArrayOf(0x00, 0x00, 0x00, 0x00)

/**
 * Example program for using PC/SC API with kotlin-pcsc.
 *
 * This program:
 *
 * 1. Connects to the first found smartcard reader
 * 2. Sends [cmd1] APDU: `00 A4 04 00 0A A0 00 00 00 62 03 01 0C 06 01`
 * 3. Displays result
 * 4. Sends [cmd2] APDU: `00 00 00 00`
 * 5. Displays result
 *
 * Reference: https://ludovicrousseau.blogspot.com/2010/04/pcsc-sample-in-different-languages.html
 */
fun main() {
    println("Sample for using PC/SC with kotlin-pcsc")
    println()
    println("Establishing a context (SCardEstablishContext)...")
    val context = Context.establish()

    println("Getting a list of available readers (SCardListReaders)...")
    val readers = context.listReaders()
    println("There were ${readers.size} reader(s) connected:")
    readers.forEachIndexed { i, reader ->
        println("$i. $reader")
    }

    println()
    if (readers.isEmpty()) {
        println("Cannot continue -- no readers connected!")
    } else {
        val reader = readers[0]
        println("Connecting to $reader (SCardConnect)...")

        val card = context.connect(reader, ShareMode.Shared, setOf(Protocol.T0, Protocol.T1))

        when (card.protocol) {
            Protocol.T0 -> println("Connected with T=0")
            Protocol.T1 -> println("Connected with T=1")
            else -> println("Protocol: ${card.protocol}")
        }

        println("Requesting status (SCardStatus)...")
        val status = card.status()
        println("Status: $status")
        println()

        println("Device: (vendor=${card.getVendorName()}) (type=${card.getIfdType()}) " +
                "(serial=${card.getIfdSerial()}) (version=${card.getIfdVersion()})")
        println("Mechanical characteristics: ${card.getMechanicalCharacteristics()}")
        println()

        println("Transmit: ${cmd1.toHex()}")
        val resp1 = card.transmit(cmd1)
        println("Response: ${resp1.toHex()}")
        println()

        println("Transmit: ${cmd2.toHex()}")
        val resp2 = card.transmit(cmd2)
        println("Response: ${resp2.toHex()}")
        println()

        println("Disconnecting from card (SCardDisconnect)")
        card.disconnect()
    }
    println("Releasing the context (SCardReleaseContext)")
    context.release()
}
