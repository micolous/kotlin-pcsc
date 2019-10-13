
import au.id.micolous.kotlin.pcsc.*

/** Converts signed [Int] values into a [ByteArray] */
fun byteArrayOf(vararg i: Int) : ByteArray = i.map(Int::toByte).toByteArray()

/** Converts a [ByteArray] to a space-separated hex string for display */
fun ByteArray.toHex(): String = map {
    it.toUByte().toString(16).padStart(2, '0').padEnd(3, ' ')
}.fold(StringBuilder(), StringBuilder::append).dropLast(1).toString()

val cmd1 = byteArrayOf(
    0x00, 0xA4, 0x04, 0x00, 0x0A, 0xA0, 0x00, 0x00, 0x00, 0x62, 0x03, 0x01,
    0x0C, 0x06, 0x01
)

val cmd2 = byteArrayOf(0x00, 0x00, 0x00, 0x00)

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
            null -> println("Unknown protocol!")
            Protocol.T0 -> println("Connected with T=0")
            Protocol.T1 -> println("Connected with T=1")
            else -> println("Protocol: ${card.protocol}")
        }

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
