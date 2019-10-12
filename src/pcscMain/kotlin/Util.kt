package au.id.micolous.kotlin.pcsc

import kotlin.collections.Collection
import kotlin.collections.MutableList
import kotlin.collections.forEachIndexed
import kotlin.sequences.Sequence
import kotlinx.cinterop.*

fun Collection<String>.asMultiString() : ByteArray {
    val buf = mutableListOf<Byte>()
    for (group in this) {
        group.encodeToByteArray(0, group.length, true).toCollection(buf)

        // Null terminator for entry
        buf.add(0)
    }

    // Null terminator for list
    buf.add(0)

    return buf.toByteArray()
}

fun ByteArray.toMultiString(): Sequence<String> {
    val array = this
    return sequence {
        var start = 0

        for (index in array.indices) {
            if (array[index] == 0.toByte()) {
                // terminator
                if (index == start) {
                    // final terminator
                    break
                }

                // Return the substring
                yield(array.decodeToString(start, index))
                start = index + 1
            }
        }
    }
}

inline fun <T : Any, R> T?.useNullablePinned(block: (Pinned<T>?) -> R): R {
    if (this == null) {
        return block(null)
    }

    return this.usePinned(block)
}