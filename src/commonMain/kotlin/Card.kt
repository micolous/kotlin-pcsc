package au.id.micolous.kotlin.pcsc

expect class Card {
    var protocol: Protocol? get

    // SCardDisconnect
    fun disconnect(disposition: DisconnectDisposition = DisconnectDisposition.Leave)

    // SCardReconnect
    fun reconnect(
        shareMode: ShareMode,
        preferredProtcols: Set<Protocol>?,
        initialization: Initialization
    )

    // SCardTransmit
    fun transmit(buffer: ByteArray) : ByteArray
}

fun Card.reconnect(shareMode: ShareMode, preferredProtocol: Protocol = Protocol.Any, initialization: Initialization)
        = reconnect(shareMode, setOf(preferredProtocol), initialization)
