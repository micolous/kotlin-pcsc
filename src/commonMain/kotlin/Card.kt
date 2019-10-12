package au.id.micolous.kotlin.pcsc

expect class Card {
    // SCardDisconnect
    fun disconnect(disposition: DisconnectDisposition = DisconnectDisposition.Leave)

    // SCardReconnect
    fun reconnect(
        shareMode: ShareMode,
        preferredProtcols: Set<Protocol>?,
        initialization: Initialization
    ): Protocol?
}

fun Card.reconnect(shareMode: ShareMode, preferredProtocol: Protocol = Protocol.Any, initialization: Initialization) : Protocol?
            = reconnect(shareMode, setOf(preferredProtocol), initialization)
