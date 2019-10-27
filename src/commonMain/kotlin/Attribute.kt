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

/**
 * Enumeration for attribute information classes.
 *
 * These correspond to `SCARD_CLASS_*` in Micrasoft's PC/SC API.
 */
enum class AttributeClass(
    /** The numeric value of this information class. */
    val v: Int) {
    /** Vendor information */
    VendorInfo(1),
    /** Communications attributes */
    Communications(2),
    /** Protocol attributes */
    Protocol(3),
    /** Power management attributes */
    PowerManagement(4),
    /** Security attributes */
    Security(5),
    /** Mechanical attributes */
    Mechanical(6),
    /** Vendor-defined attributes */
    VendorDefined(7),
    IFDProtocol(8),
    ICCState(9),
    /** Performance counters */
    Perf(0x7ffe),
    System(0x7fff);
}

/**
 * Card attribute IDs.
 *
 * These are described in PC/SC Specification Part 3, Section 3 (Functionality Requirements).
 *
 * @see [Card.getAttrib]
 */
enum class Attribute(
    /** The class of the attribute. */
    val cls: AttributeClass,
    /** The tag ID of the attribute. */
    val tag: Int
) {
    /** @see Card.getVendorName */
    VendorName(AttributeClass.VendorInfo, 0x0100),
    /** @see Card.getIfdType */
    VendorIfdType(AttributeClass.VendorInfo, 0x0101),
    /** @see Card.getIfdVersion */
    VendorIfdVersion(AttributeClass.VendorInfo, 0x0102),
    /** @see Card.getIfdSerial */
    VendorIfdSerial(AttributeClass.VendorInfo, 0x0103),
    /** @see Card.getMechanicalCharacteristics */
    MechanicalCharacteristics(AttributeClass.Mechanical, 0x0150),
    ;
    // TODO: implement all the other things

    /** The attribute definition in numeric form */
    val num: Long = (cls.v.toLong() shl 16) or (tag.toLong() and 0xffffL)
}

/**
 * Represents a version number, given in response to a [Attribute.VendorIfdVersion] request.
 */
data class Version internal constructor(
    /** The major version number. */
    val major: Int,
    /** The minor version number. */
    val minor: Int,
    /** The build number. */
    val build: Int
) {
    internal constructor(b: ByteArray) : this(
        major = b.getInt(0, 1),
        minor = b.getInt(1, 1),
        build = b.getInt(2, 2)
    )

    /** Converts the version number to its string representation. */
    override fun toString() = "$major.$minor.$build"
}

/**
 * Describes the mechanical characteristics of a card reader.
 */
data class MechanicalCharacteristics internal constructor(private val c: Long) {
    internal constructor(b: ByteArray) : this(b.getLong(0, 4))

    private fun hasBits(bits: Long) = (c and bits) == bits

    /** Reader has a card swallowing mechanism */
    val cardSwallowing = hasBits(0x01)
    /** Reader has a card ejection mechanism */
    val cardEjection = hasBits(0x02)
    /** Reader has a card capture mechanism */
    val cardCapture = hasBits(0x04)
    /** Reader supports contactless communication */
    val contactless = hasBits(0x08)
}

/**
 * Gets an attribute of the [Card] by its well-known [Attribute] identifier.
 */
fun Card.getAttrib(attribute: Attribute) = getAttrib(attribute.num)

/**
 * Gets an attribute of the [Card] using an [AttributeClass] and tag (as [Int]).
 *
 * @param cls The attribute's information class
 * @param tag The attribute's tag ID, must be between 0 and 0xffff
 */
fun Card.getAttrib(cls: AttributeClass, tag: Int) = getAttrib(cls.v, tag)

/** Gets the IFD's vendor's name */
fun Card.getVendorName() = getAttrib(Attribute.VendorName)?.decodeToString()

/** Gets the vendor-specified IFD type */
fun Card.getIfdType() = getAttrib(Attribute.VendorIfdType)?.decodeToString()

/** Gets the vendor-specified IFD version */
fun Card.getIfdVersion() =
    getAttrib(Attribute.VendorIfdVersion)?.asBigEndian()?.let(::Version)

/** Gets the IFD serial number */
fun Card.getIfdSerial() = getAttrib(Attribute.VendorIfdSerial)?.decodeToString()

/**
 * Gets the mechanical characteristics of the IFD.
 *
 * @see MechanicalCharacteristics
 */
fun Card.getMechanicalCharacteristics() = (
        getAttrib(Attribute.MechanicalCharacteristics)?.asBigEndian()?.let(
            ::MechanicalCharacteristics))
