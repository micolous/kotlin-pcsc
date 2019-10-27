/*
 * Context.kt
 * Native implementation of SCARDCONTEXT PC/SC operations
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

// Based on winsmcard.h

// SCARD_CLASS_*
enum class AttributeClass(val v: Int) {
    VendorInfo(1),
    Communications(2),
    Protocol(3),
    PowerManagement(4),
    Security(5),
    Mechanical(6),
    VendorDefined(7),
    IFDProtocol(8),
    ICCState(9),
    Perf(0x7ffe),
    System(0x7fff);
}

enum class Attribute(val cls: AttributeClass, val tag: Int) {
    VendorName(AttributeClass.VendorInfo, 0x0100),
    VendorIfdType(AttributeClass.VendorInfo, 0x0101),
    VendorIfdVersion(AttributeClass.VendorInfo, 0x0102),
    VendorIfdSerial(AttributeClass.VendorInfo, 0x0103);

    /** The attribute definition in numeric form */
    val num: Long = (cls.v.toLong() shl 16) or (tag.toLong() and 0xffffL)
}

data class Version internal constructor(val major: Int, val minor: Int, val build: Int) {
    internal constructor(b: ByteArray) : this(
        major = b.getInt(0, 1),
        minor = b.getInt(1, 1),
        build = b.getInt(2, 2))

    override fun toString() = "$major.$minor.$build"
}

fun Card.getAttrib(attribute: Attribute) = getAttrib(attribute.num)
fun Card.getAttrib(cls: AttributeClass, tag: Int) = getAttrib(cls.v, tag)

fun Card.getVendorName() = getAttrib(Attribute.VendorName)?.decodeToString()
fun Card.getIfdType() = getAttrib(Attribute.VendorIfdType)?.decodeToString()
fun Card.getIfdVersion() =
    getAttrib(Attribute.VendorIfdVersion)?.asBigEndian()?.let { Version(it) }
fun Card.getIfdSerial() = getAttrib(Attribute.VendorIfdSerial)?.decodeToString()

