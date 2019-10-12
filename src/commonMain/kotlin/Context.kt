package au.id.micolous.kotlin.pcsc

expect class Context {
    // SCardReleaseContext
    fun release()

    // SCardIsValidContext
    fun isValid() : Boolean

    // SCardListReaders
    fun listReaders(groups: List<String>? = null) : List<String>

    // SCardConnect
    fun connect(reader: String, shareMode: ShareMode, preferredProtcols: Set<Protocol>?) : Pair<Card, Protocol?>

    companion object {
        // SCardEstablishContext
        fun establish(scope: Scope = Scope.User) : Context
    }
}

fun Context.connect(reader: String, shareMode: ShareMode, preferredProtocol: Protocol = Protocol.Any) : Pair<Card, Protocol?>
        = connect(reader, shareMode, setOf(preferredProtocol))

