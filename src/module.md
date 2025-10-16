# Module kotlin-pcsc

[kotlin-pcsc][] is a Kotlin Multiplatform library for accessing smart cards via
the PC/SC API on the Java JVM and native applications (on Linux, macOS and
Windows).

[kotlin-pcsc]: https://github.com/micolous/kotlin-pcsc

# Package au.id.micolous.kotlin.pcsc

[kotlin-pcsc][] is a Kotlin Multiplatform library for accessing smart cards via
the PC/SC API on the Java JVM and native applications (on Linux, macOS and
Windows).

[kotlin-pcsc]: https://github.com/micolous/kotlin-pcsc

It takes some small liberties with the PC/SC API to make it object oriented, and
easier to use:

PC/SC function          | `kotlin-pcsc` function(s)
----------------------- | -------------------------
`SCardBeginTransaction` | [Card.beginTransaction][]
`SCardCancel`           | [Context.cancel][]
`SCardConnect`          | [Context.connect][]
`SCardControl`          | [Card.control][]
`SCardDisconnect`       | [Card.disconnect][]
`SCardEndTransaction`   | [Card.endTransaction][]
`SCardEstablishContext` | [Context.establish][]
`SCardGetAttrib`        | [Card.getAttrib][]; plus [Card.getIfdSerial][], [Card.getIfdType][], [Card.getIfdVersion][], [Card.getMechanicalCharacteristics][], [Card.getVendorName][]
`SCardGetStatusChange`  | [Context.getStatusChange][]; plus [Context.getAllReaderStatus][], [Context.getStatus][]
`SCardIsValidContext`   | [Context.isValid][]
`SCardListReaders`      | [Context.listReaders][]
`SCardReconnect`        | [Card.reconnect][]
`SCardReleaseContext`   | [Context.release][]
`SCardStatus`           | [Card.status][]
`SCardTransmit`         | [Card.transmit][]

### Unimplemented functions

These functions aren't available in `kotlin-pcsc`:

* `SCardCancelTransaction` (on Windows, this is "reserved for future use")
* `SCardListReaderGroups`
* `SCardSetAttrib`
* `SCardSetTimeout`

Pull requests are welcome!

Windows defines a number of extra APIs which do not have equivalents on other
platforms. Supporting these is not a priority.

### Public API

Only the `commonMain` module is part of the public API.

Anything that **only** appears in `jnaMain`, `nativeMain`, `nativeInterop` or
`mingwMain` is an internal implementation detail, and is subject to change
without warning.
